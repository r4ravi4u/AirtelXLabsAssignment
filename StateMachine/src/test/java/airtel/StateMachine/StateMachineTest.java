package airtel.StateMachine;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StateMachineTest {

	@BeforeAll
	static void TestStarts()	{
		System.out.println("State Machine Test Started");
	}
	
	@Test
	void test() {
		StateMachine sm = new StateMachine();
		
		List<Integer> start = new ArrayList<>(Arrays.asList(1,2,3,4,5,7,8));
		List<Integer>[] ends = (ArrayList<Integer>[]) new ArrayList[start.size()];
		for(int i = 0; i < start.size(); i++)	{
			ends[i] = new ArrayList<>();
		}
		ends[0].add(2);	ends[0].add(3);	ends[1].add(4);
		ends[1].add(5);	ends[1].add(6);
		ends[2].add(7);	ends[2].add(8);
		ends[3].add(9);
		ends[4].add(9);
		ends[5].add(10);
		ends[6].add(10);
		
		List<List<Integer>> end = new ArrayList<>();
		for(int i = 0; i < start.size(); i++)	{
			end.add(ends[i]);
		}
		
		assertEquals("YES ; path : 1->2->4", sm.getAllPaths(start, end, 1, 4));
		
		assertEquals("YES ; path : 2->4->9 or 2->5->9", sm.getAllPaths(start, end, 2, 9));
		
		assertEquals(Constants.NO, sm.getAllPaths(start, end, 3, 4));
	}
	
	@AfterAll
	static void TestEnds()	{
		System.out.println("State Machine Test Ends");
	}

}
