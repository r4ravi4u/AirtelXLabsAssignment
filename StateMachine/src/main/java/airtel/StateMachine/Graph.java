package airtel.StateMachine;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Graph {
	private Map<Integer, List<Integer>> map;

	public Graph() {
		map = new HashMap<>();
	}
	
	public void addEdges(int start, List<Integer> end)	{
		//merge 2 lists in case start repeats
		if(map.containsKey(start))	{
			List<Integer> val = map.get(start);
			val.addAll(end);
			return;
		}
		map.put(start, end);
	}
	
	public boolean contains(int src)
	{
		return map.containsKey(src);
	}

	public List<Integer> getAdjacentVertices(Integer v) {
		return map.get(v);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		List<Integer> list = null;
		for (Integer v : map.keySet()) {
			list = map.get(v);
			if (list == null)
				continue;
			
			str.append(v + "->");
			for (int i : list) {
				str.append(i);
				str.append(", ");
			}
			str.append("\n");
		}
		return str.toString();
	}
}