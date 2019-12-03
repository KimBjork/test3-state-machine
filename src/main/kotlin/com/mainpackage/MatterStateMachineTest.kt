package com.mainpackage

import com.tinder.StateMachine
import org.assertj.core.api.Assertions
import org.junit.Test

class MatterStateMachineTest {

        private val stateMachine = StateMachine.create<State, Event, SideEffect> {
            initialState(State.Solid)
            state<State.Solid> {
                on<Event.OnMelted> {
                    transitionTo(State.Liquid, SideEffect.LogMelted)
                }
            }
            state<State.Liquid> {
                on<Event.OnFrozen> {
                    transitionTo(State.Solid, SideEffect.LogFrozen)
                }
                on<Event.OnVaporized> {
                    transitionTo(State.Gas, SideEffect.LogVaporized)
                }
            }
            state<State.Gas> {
                on<Event.OnCondensed> {
                    transitionTo(State.Liquid, SideEffect.LogCondensed)
                }
            }
            onTransition {
                val validTransition = it as? StateMachine.Transition.Valid ?: return@onTransition
                when (validTransition.sideEffect) {

                }
            }
        }

        @Test
        fun initialState_shouldBeSolid() {
            // Then
            Assertions.assertThat(stateMachine.state).isEqualTo(State.Solid)
        }

        @Test
        fun givenStateIsSolid_onMelted_shouldTransitionToLiquidStateAndLog() {
            // Given
            val stateMachine = givenStateIs(State.Solid)

            // When
            val transition = stateMachine.transition(Event.OnMelted)

            // Then
            Assertions.assertThat(stateMachine.state).isEqualTo(State.Liquid)
            Assertions.assertThat(transition).isEqualTo(
                    StateMachine.Transition.Valid(State.Solid, Event.OnMelted, State.Liquid, SideEffect.LogMelted)
            )
        }

        @Test
        fun givenStateIsLiquid_onFroze_shouldTransitionToSolidStateAndLog() {
            // Given
            val stateMachine = givenStateIs(State.Liquid)

            // When
            val transition = stateMachine.transition(Event.OnFrozen)

            // Then
            Assertions.assertThat(stateMachine.state).isEqualTo(State.Solid)
            Assertions.assertThat(transition).isEqualTo(
                    StateMachine.Transition.Valid(State.Liquid, Event.OnFrozen, State.Solid, SideEffect.LogFrozen)
            )
        }

        @Test
        fun givenStateIsLiquid_onVaporized_shouldTransitionToGasStateAndLog() {
            // Given
            val stateMachine = givenStateIs(State.Liquid)

            // When
            val transition = stateMachine.transition(Event.OnVaporized)

            // Then
            Assertions.assertThat(stateMachine.state).isEqualTo(State.Gas)
            Assertions.assertThat(transition).isEqualTo(
                    StateMachine.Transition.Valid(State.Liquid, Event.OnVaporized, State.Gas, SideEffect.LogVaporized)
            )
        }

        @Test
        fun givenStateIsGas_onCondensed_shouldTransitionToLiquidStateAndLog() {
            // Given
            val stateMachine = givenStateIs(State.Gas)

            // When
            val transition = stateMachine.transition(Event.OnCondensed)

            // Then
            Assertions.assertThat(stateMachine.state).isEqualTo(State.Liquid)
            Assertions.assertThat(transition).isEqualTo(
                    StateMachine.Transition.Valid(State.Gas, Event.OnCondensed, State.Liquid, SideEffect.LogCondensed)
            )
        }

        private fun givenStateIs(state: State): StateMachine<State, Event, SideEffect> {
            return stateMachine.with { initialState(state) }
        }

        companion object {
            const val ON_MELTED_MESSAGE = "I melted"
            const val ON_FROZEN_MESSAGE = "I froze"
            const val ON_VAPORIZED_MESSAGE = "I vaporized"
            const val ON_CONDENSED_MESSAGE = "I condensed"

            sealed class State {
                object Solid : State()
                object Liquid : State()
                object Gas : State()
            }

            sealed class Event {
                object OnMelted : Event()
                object OnFrozen : Event()
                object OnVaporized : Event()
                object OnCondensed : Event()
            }

            sealed class SideEffect {
                object LogMelted : SideEffect()
                object LogFrozen : SideEffect()
                object LogVaporized : SideEffect()
                object LogCondensed : SideEffect()
            }

            interface Logger {
                fun log(message: String)
            }
        }
}