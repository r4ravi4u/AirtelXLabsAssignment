package airtel.ClosestAddressMatch;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClosestAddressMatchTest {

	@BeforeAll
	static void TestStarts()	{
		System.out.println("Closest Address Match Test Started");
	}
	
	@Test
	void test() {
		ClosestAddressMatch cam = new ClosestAddressMatch();
    	
    	List<String> M = new ArrayList<>();
		M.add("6480, Sector C6, Vasant Kunj");
		M.add("Plot 16, Udyog Vihar Phase -4, Gurgaon");
		M.add("Plot 19, Udyog Vihar Phase -4, Gurgaon");
		M.add("8231, Sector C8, Vasant Kunj");
		M.add("C-6/6280, Vasant Kunj");
		M.add("Sector 5, Pitampura");
		
		String N1 = "6279, Sector C6, Vasant Kunj";
		String N2 = "Plot 18, National Highway  8, Udyog Vihar Phase -4, Gurgaon";
		String N3 = "Pitampura";
		
		assertEquals("C-6/6280, Vasant Kunj", cam.closestAddressMatch(M, N1));
		
		assertEquals("Plot 19, Udyog Vihar Phase -4, Gurgaon", cam.closestAddressMatch(M, N2));
		
		assertEquals("Sector 5, Pitampura", cam.closestAddressMatch(M, N3));
	}
	
	@AfterAll
	static void TestEnds()	{
		System.out.println("Closest Address Match Test Ends");
	}


}
