package com.mainpackage

import com.tinder.StateMachine
import org.assertj.core.api.Assertions
import org.junit.Test

class TurnstileStateMachineTest {

    private val stateMachine = StateMachine.create<State, Event, Command> {
        initialState(State.Locked(credit = 0))
        state<State.Locked> {
            on<Event.InsertCoin> {
                val newCredit = credit + it.value
                if (newCredit >= FARE_PRICE) {
                    transitionTo(State.Unlocked, Command.OpenDoors)
                } else {
                    transitionTo(State.Locked(newCredit))
                }
            }
            on<Event.AdmitPerson> {
                dontTransition(Command.SoundAlarm)
            }
            on<Event.MachineDidFail> {
                transitionTo(State.Broken(this), Command.OrderRepair)
            }
        }
        state<State.Unlocked> {
            on<Event.AdmitPerson> {
                transitionTo(State.Locked(credit = 0), Command.CloseDoors)
            }
        }
        state<State.Broken> {
            on<Event.MachineRepairDidComplete> {
                transitionTo(oldState)
            }
        }
    }

    @Test
    fun initialState_shouldBeLocked() {
        // Then
        Assertions.assertThat(stateMachine.state).isEqualTo(State.Locked(credit = 0))
    }

    @Test
    fun givenStateIsLocked_whenInsertCoin_andCreditLessThanFairPrice_shouldTransitionToLockedState() {
        // When
        val transition = stateMachine.transition(Event.InsertCoin(10))

        // Then
        Assertions.assertThat(stateMachine.state).isEqualTo(State.Locked(credit = 10))
        Assertions.assertThat(transition).isEqualTo(
                StateMachine.Transition.Valid(
                        State.Locked(credit = 0),
                        Event.InsertCoin(10),
                        State.Locked(credit = 10),
                        null
                )
        )
    }

    @Test
    fun givenStateIsLocked_whenInsertCoin_andCreditEqualsFairPrice_shouldTransitionToUnlockedStateAndOpenDoors() {
        // Given
        val stateMachine = givenStateIs(State.Locked(credit = 35))

        // When
        val transition = stateMachine.transition(Event.InsertCoin(15))

        // Then
        Assertions.assertThat(stateMachine.state).isEqualTo(State.Unlocked)
        Assertions.assertThat(transition).isEqualTo(
                StateMachine.Transition.Valid(
                        State.Locked(credit = 35),
                        Event.InsertCoin(15),
                        State.Unlocked,
                        Command.OpenDoors
                )
        )
    }

    @Test
    fun givenStateIsLocked_whenInsertCoin_andCreditMoreThanFairPrice_shouldTransitionToUnlockedStateAndOpenDoors() {
        // Given
        val stateMachine = givenStateIs(State.Locked(credit = 35))

        // When
        val transition = stateMachine.transition(Event.InsertCoin(20))

        // Then
        Assertions.assertThat(stateMachine.state).isEqualTo(State.Unlocked)
        Assertions.assertThat(transition).isEqualTo(
                StateMachine.Transition.Valid(
                        State.Locked(credit = 35),
                        Event.InsertCoin(20),
                        State.Unlocked,
                        Command.OpenDoors
                )
        )
    }

    @Test
    fun givenStateIsLocked_whenAdmitPerson_shouldTransitionToLockedStateAndSoundAlarm() {
        // Given
        val stateMachine = givenStateIs(State.Locked(credit = 35))

        // When
        val transition = stateMachine.transition(Event.AdmitPerson)

        // Then
        Assertions.assertThat(stateMachine.state).isEqualTo(State.Locked(credit = 35))
        Assertions.assertThat(transition).isEqualTo(
                StateMachine.Transition.Valid(
                        State.Locked(credit = 35),
                        Event.AdmitPerson,
                        State.Locked(credit = 35),
                        Command.SoundAlarm
                )
        )
    }

    @Test
    fun givenStateIsLocked_whenMachineDidFail_shouldTransitionToBrokenStateAndOrderRepair() {
        // Given
        val stateMachine = givenStateIs(State.Locked(credit = 15))

        // When
        val transitionToBroken = stateMachine.transition(Event.MachineDidFail)

        // Then
        Assertions.assertThat(stateMachine.state).isEqualTo(State.Broken(oldState = State.Locked(credit = 15)))
        Assertions.assertThat(transitionToBroken).isEqualTo(
                StateMachine.Transition.Valid(
                        State.Locked(credit = 15),
                        Event.MachineDidFail,
                        State.Broken(oldState = State.Locked(credit = 15)),
                        Command.OrderRepair
                )
        )
    }

    @Test
    fun givenStateIsUnlocked_whenAdmitPerson_shouldTransitionToLockedStateAndCloseDoors() {
        // Given
        val stateMachine = givenStateIs(State.Unlocked)

        // When
        val transition = stateMachine.transition(Event.AdmitPerson)

        // Then
        Assertions.assertThat(stateMachine.state).isEqualTo(State.Locked(credit = 0))
        Assertions.assertThat(transition).isEqualTo(
                StateMachine.Transition.Valid(
                        State.Unlocked,
                        Event.AdmitPerson,
                        State.Locked(credit = 0),
                        Command.CloseDoors
                )
        )
    }

    @Test
    fun givenStateIsBroken_whenMachineRepairDidComplete_shouldTransitionToLockedState() {
        // Given
        val stateMachine = givenStateIs(State.Broken(oldState = State.Locked(credit = 15)))

        // When
        val transition = stateMachine.transition(Event.MachineRepairDidComplete)

        // Then
        Assertions.assertThat(stateMachine.state).isEqualTo(State.Locked(credit = 15))
        Assertions.assertThat(transition).isEqualTo(
                StateMachine.Transition.Valid(
                        State.Broken(oldState = State.Locked(credit = 15)),
                        Event.MachineRepairDidComplete,
                        State.Locked(credit = 15),
                        null
                )
        )
    }

    private fun givenStateIs(state: State): StateMachine<State, Event, Command> {
        return stateMachine.with { initialState(state) }
    }

    companion object {
        private const val FARE_PRICE = 50

        sealed class State {
            data class Locked(val credit: Int) : State()
            object Unlocked : State()
            data class Broken(val oldState: State) : State()
        }

        sealed class Event {
            data class InsertCoin(val value: Int) : Event()
            object AdmitPerson : Event()
            object MachineDidFail : Event()
            object MachineRepairDidComplete : Event()
        }

        sealed class Command {
            object SoundAlarm : Command()
            object CloseDoors : Command()
            object OpenDoors : Command()
            object OrderRepair : Command()
        }
    }
}