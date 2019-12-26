package airtel.StateMachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

interface Constants	{
	String NO = "NO";
	String YES = "YES ; path : ";
}

public class StateMachine {
	
	private String getAllPathsUtil(Graph g, int src, int dest)	{
		
		Queue<Set<Integer>> q = new LinkedList<>();
		Set<Integer> path = new LinkedHashSet<>();
		
		path.add(src);
		q.offer(path);
		
		boolean flag = false;
		
		StringBuilder result = new StringBuilder();
		result.append(Constants.YES);
		while(!q.isEmpty())	{
			path=q.poll();
			if(path == null)
				continue;
			
			int last = path.stream().skip(path.size()-1).findFirst().get();
			
			if(last == dest)	{
				//System.out.println(path);
				if(flag)
					result.append(" or ");
				int len = path.size() - 1;
				for(int s : path)	{
					result.append(s);
					if(len != 0)
						result.append("->");
					len--;
				}
					
				flag = true;
			}
			
			List<Integer> adj = g.getAdjacentVertices(last);
			if(adj == null)
				continue;
			for(int i = 0; i < adj.size(); i++)	{
				if(path.contains(adj.get(i)))
					continue;
				
				Set<Integer> curr = new LinkedHashSet<>(path);
				curr.add(adj.get(i));
				q.offer(curr);
			}				
		}
		
		if(!flag)
			return Constants.NO;
		
		return result.toString();
	}
	
	public String getAllPaths(List<Integer> start, List<List<Integer>> end, int src, int dest) {
		
		if(start == null || end == null)
			return Constants.NO;
		
		int lenStart = start.size();
		int lenEnd = end.size();
		
		if(lenStart != lenEnd)
			return Constants.NO;
		
		Graph g = new Graph();
		for(int i = 0; i < lenStart; i++)
			g.addEdges(start.get(i), end.get(i));
		
		//System.out.println(g);
		if(!g.contains(src))
			return Constants.NO;
		
		if(src == dest)
			return Constants.YES + src;
		
		//Use BFS (better than DFS for this case)
		return getAllPathsUtil(g, src, dest);		
	}

	
	public static void main(String[] args) {
		List<Integer> start = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 7, 8));
		List<Integer>[] ends = (ArrayList<Integer>[]) new ArrayList[start.size()];
		for (int i = 0; i < start.size(); i++) {
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
		for (int i = 0; i < start.size(); i++) {
			end.add(ends[i]);
		}

		System.out.println(new StateMachine().getAllPaths(start, end, 2, 9));
	}
	 
}
