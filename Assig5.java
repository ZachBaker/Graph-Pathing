import java.util.*;
import java.io.*;

public class Assig5{

	EdgeWeightedDigraph network;
	int vertices, edges;

	public Assig5(String fileName) throws FileNotFoundException{

		File file = new File(fileName);
		Scanner fileScanner = new Scanner(file);

		vertices = fileScanner.nextInt();
		edges = fileScanner.nextInt();

		network = new EdgeWeightedDigraph(vertices);

		fileScanner.nextLine();

		while(fileScanner.hasNext())
		{
			String newLine = fileScanner.nextLine();

			String [] line = newLine.split(" ");

			int vertex1 = Integer.parseInt(line[0]);
			int vertex2 = Integer.parseInt(line[1]);
			int edgeWeight = Integer.parseInt(line[2]);

			DirectedEdge newEdge = new DirectedEdge(vertex1, vertex2, edgeWeight);
			DirectedEdge otherEdge = new DirectedEdge(vertex2,vertex1,edgeWeight);
			network.addEdge(newEdge);
			network.addEdge(otherEdge);
		}

		System.out.println("The network has been established");

		Scanner inScan = new Scanner(System.in);

		System.out.println("Enter your command");
		String userInput = inScan.nextLine();

		while(!userInput.equals("Q")){

			String [] vals = userInput.split(" ");

			if(userInput.equals("R")){

				network.upDownStatus();
				System.out.println("The current active network includes the following " + network.V() + " nodes and " + network.E() + " edges:");
				System.out.println(network.toString());
			}

			else if(userInput.equals("M")){

				EdgeWeightedDigraph graphCopy = new EdgeWeightedDigraph(vertices);

				for(DirectedEdge e : network.edges())
					graphCopy.changeEdge(e.from(),e.to(),e.weight());

				LazyPrimMSTTrace prim = new LazyPrimMSTTrace(graphCopy);

				System.out.println("The MST is composed of the following edges:");

				for(Edge e : prim.edges())
					System.out.println(e.toString());
				System.out.println();
			}

			else{

				String firstChar = new String(new char[]  {userInput.charAt(0)});

				if(firstChar.equals("S")){

					int startVertex = Integer.parseInt(vals[1]);
					int endVertex = Integer.parseInt(vals[2]);

					DijkstraSP shortestPath = new DijkstraSP(network, startVertex);

					System.out.println("The shortest path from vertex " + startVertex + " to vertex " + endVertex + " is:");

					if(shortestPath.pathTo(endVertex) != null){
						System.out.println(shortestPath.pathTo(endVertex));
						System.out.println("The total weight of this path is: " + shortestPath.distTo(endVertex));
					}
					else
						System.out.println("The path between " + startVertex + " and " + endVertex + " does not exsist.");
				}

				else if(firstChar.equals("P")){

					//try{
					int startVertex = Integer.parseInt(vals[1]);
					int endVertex = Integer.parseInt(vals[2]);
					double weightLimit = Double.parseDouble(vals[3]);

					network.allPaths(startVertex,endVertex,weightLimit);
					//}

					//catch(Exception e){
					//	System.out.println("Operation was not formatted correctly. Please try again");
					//}

				}

				else if(firstChar.equals("D")){

					try{
					int removedNode = Integer.parseInt(vals[1]);
					network.takeDownVertex(removedNode);
					}

					catch(Exception e){
						System.out.println("Operation was not formatted correctly. Please try again");
					}
				}

				else if(firstChar.equals("U")){

					try{
					int restoredNode = Integer.parseInt(vals[1]);
					network.restoreVertex(restoredNode);
					}

					catch(Exception e){
						System.out.println("Operation was not formatted correctly. Please try again");
					}
				}

				else if(firstChar.equals("C")){

					try{
					int startVertex = Integer.parseInt(vals[1]);
					int endVertex = Integer.parseInt(vals[2]);
					double newWeight = Double.parseDouble(vals[3]);
					network.changeEdge(startVertex,endVertex,newWeight);
					}

					catch(Exception e){
						System.out.println("Operation was not formatted correctly. Please try again");
					}
				}

				else{
					System.out.println("Incorrect Command");
					System.out.println();
				}

			}
			System.out.println();
			System.out.println("Enter your command");
			userInput = inScan.nextLine();
		}
	}

	public static void main(String [] args)throws FileNotFoundException{
		Assig5 runner = new Assig5(args[0]);
	}
}