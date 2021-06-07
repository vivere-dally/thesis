import { ActionPayload, ActionState, ActionType, Entity, newReducer, StateCrud } from '../entity';

interface TestEntity extends Entity<number> { testProperty?: string; }
interface TestState extends StateCrud<TestEntity, number> { }

describe('newReducer', () => {

    let reducer: (state: TestState, actionPayload: ActionPayload) => TestState, testState: TestState;
    beforeEach(() => {
        reducer = newReducer<TestState, TestEntity, number>();
        testState = { executing: false };
    })

    it('ActionState.STARTED', () => {
        testState.actionError = new Error('test');

        testState = reducer(testState, { actionState: ActionState.STARTED, actionType: ActionType.GET });

        expect(testState.executing).toBe(true);
        expect(testState.actionType).toBe(ActionType.GET);
        expect(testState.actionError).toBe(null);
    });

    it('ActionState.FAILED', () => {
        const expected = new Error('test failed');
        testState.executing = true;

        testState = reducer(testState, { actionState: ActionState.FAILED, data: expected });

        expect(testState.executing).toBe(false);
        expect(testState.actionError).toBe(expected);
    });

    it.each([
        ActionType.GET,
        ActionType.GET_ONE
    ])('ActionState.SUCCEEDED.%s', (actionType: ActionType) => {
        const expected: TestEntity[] = [{ id: 1 }];
        testState.executing = true;

        testState = reducer(testState, { actionState: ActionState.SUCCEEDED, actionType: actionType, data: expected });

        expect(testState.executing).toBe(false);
        expect(testState.data).toEqual(expected);
    })

    it('ActionState.SUCCEEDED.GET_PAGED', () => {
        const expected: TestEntity[] = [{ id: 1 }, { id: 2 }];
        testState.data = [expected[0]];
        testState.executing = true;

        testState = reducer(testState, { actionState: ActionState.SUCCEEDED, actionType: ActionType.GET_PAGED, data: [expected[1]] });

        expect(testState.executing).toBe(false);
        expect(testState.data).toEqual(expected);
    });

    it('ActionState.SUCCEEDED.POST', () => {
        const expected: TestEntity[] = [{ id: 1 }, { id: 2 }];
        testState.data = [expected[1]];
        testState.executing = true;

        testState = reducer(testState, { actionState: ActionState.SUCCEEDED, actionType: ActionType.POST, data: expected[0] });

        expect(testState.executing).toBe(false);
        expect(testState.data).toEqual(expected);
    });

    it('ActionState.SUCCEEDED.PUT', () => {
        const expected: TestEntity = { id: 1, testProperty: "expected" };
        testState.data = [{ id: 1, testProperty: "actual" }];
        testState.executing = true;

        testState = reducer(testState, { actionState: ActionState.SUCCEEDED, actionType: ActionType.PUT, data: expected });

        expect(testState.executing).toBe(false);
        expect(testState.data).toEqual([expected]);
    });

    it('ActionState.SUCCEEDED.DELETE', () => {
        const expected: number = 1;
        testState.data = [{ id: expected, testProperty: "expected" }];
        testState.executing = true;

        testState = reducer(testState, { actionState: ActionState.SUCCEEDED, actionType: ActionType.DELETE, data: { id: expected } });

        expect(testState.executing).toBe(false);
        expect(testState.data!.length).toBe(0);
    });
});
