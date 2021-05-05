
export interface Entity<T> {
    id?: T;
}

export enum ActionState {
    STARTED = "STARTED",
    SUCCEEDED = "SUCCEEDED",
    FAILED = "FAILED"
}

export enum ActionType {
    GET = "GET",
    GET_ONE = "GET_ONE",
    GET_PAGED = "GET_PAGED",
    POST = "POST",
    PUT = "PUT",
    DELETE = "DELETE"
}

export interface ActionPayload {
    actionState?: ActionState;
    actionType?: ActionType;
    data?: any;
}

export interface State<E extends Entity<T>, T> {
    data?: E[];
    executing: boolean;
    actionType?: ActionType;
    actionError?: Error | null;
}

export interface StateCrud<E extends Entity<T>, T> extends State<E, T> {
    get?: () => Promise<E | void>;
    getOne?: (t: T) => Promise<E | void>;
    post?: (e: E) => Promise<E | void>;
    put?: (e: E) => Promise<E | void>;
    delete?: (t: T) => Promise<E | void>;
}

export function newReducer<S extends State<E, T>, E extends Entity<T>, T>(): (state: S, actionPayload: ActionPayload) => S {
    const reducer: (state: S, actionPayload: ActionPayload) => S = (state, { actionState, actionType, data }) => {
        switch (actionState) {
            case ActionState.STARTED:
                return { ...state, executing: true, actionType: actionType, actionError: null }
            case ActionState.FAILED:
                return { ...state, executing: false, actionError: data }
            case ActionState.SUCCEEDED:
                switch (actionType) {
                    case ActionType.GET:
                    case ActionType.GET_ONE:
                        return { ...state, executing: false, data: data }
                    case ActionType.GET_PAGED:
                        return {
                            ...state, executing: false, data: ((): Entity<T>[] => {
                                const stateData = state.data || []; // TODO [...(state.data || [])] maybe???
                                return stateData.concat(data);
                            })()
                        }
                    case ActionType.POST:
                    case ActionType.PUT:
                    case ActionType.DELETE:
                        return {
                            ...state, executing: false, data: ((): Entity<T>[] => {
                                const stateData = state.data || []; // TODO [...(state.data || [])] maybe???
                                const index = stateData.findIndex(it => it.id === data.id);
                                switch (actionType) {
                                    case ActionType.POST:
                                        if (index === -1) {
                                            stateData.splice(0, 0, data);
                                        }

                                        break;
                                    case ActionType.PUT:
                                        if (index !== -1) {
                                            stateData[index] = data;
                                        }

                                        break;
                                    case ActionType.DELETE:
                                        if (index !== -1) {
                                            stateData.splice(index, 1);
                                        }
                                }

                                return stateData;
                            })()
                        }

                    default:
                        return { ...state, executing: false }
                }
            default:
                return state;
        }
    }

    return reducer;
}
