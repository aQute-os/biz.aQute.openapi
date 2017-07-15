package aQute.www.fsm.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class FSM<State extends Enum<State>, Event extends Enum<Event>, Context> {

	final Transition[][]	transitions;
	volatile State			state;

	@SuppressWarnings("rawtypes")
	static class Transition {
		Enum			to;
		List<Consumer>	actions;
		Function		trigger;
	}

	public FSM(Transition[][] transitions, State initial) {
		this.transitions = transitions;
		state = initial;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void event(Event event, Context context) {
		Transition t;
		synchronized (transitions) {
			t = transitions[state.ordinal()][event.ordinal()];
			if (t == null) {
				return;
			} else {
				state = (State) t.to;
			}
		}
		for (Consumer r : t.actions) {
			r.accept(context);
		}
	}

	public static class Builder<State extends Enum<State>, Event extends Enum<Event>, Context, Result> {
		Transition[][]	transitions;
		private int		stateLength;
		private int		eventLength;

		Builder(Class<State> states, Class<Event> events) {
			stateLength = states.getEnumConstants().length;
			eventLength = events.getEnumConstants().length;
			transitions = new Transition[stateLength][eventLength];
		}

		public Builder<State, Event, Context,Result> transition(State from, Event event, State to,
				Function<Context, Result> action) {
			Transition t = transitions[from.ordinal()][event.ordinal()];
			if (t == null) {
				t = transitions[from.ordinal()][event.ordinal()] = new Transition();
			}
			if (t.to != null) {
				throw new IllegalArgumentException(
						"Transition already defined " + from + "->" + event + "->" + to + " was " + t.to);
			}

			t.trigger = action;
			return this;
		}

		public Builder<State, Event, Context,Result> onEnter(State state, Consumer<Context> action) {
			for (int s = 0; s < stateLength; s++) {

				boolean isAlsoSource = s == state.ordinal();
				if (isAlsoSource)
					continue;

				for (int e = 0; e < eventLength; e++) {
					Transition t = transitions[s][e];
					if (t == null)
						continue;

					if (t.to == state) {
						t.actions.add(action);
					}
				}

			}
			return this;
		}

		public Builder<State, Event, Context,Result> onExit(State state, Consumer<Context> action) {
			int s = state.ordinal();

			for (int e = 0; e < eventLength; e++) {
				Transition t = transitions[s][e];
				if (t.to != state) {
					t.actions.add(action);
				}
			}

			return this;
		}

		FSM<State, Event, Context> build(State initial) {
			return new FSM<State, Event, Context>(transitions, initial);
		}
	}

}
