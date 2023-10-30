import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Edmond {

    static class Edge {
        int from, to;
        double weight;

        public Edge(int from, int to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    static class Graph {
        int V; // Number of vertices
        List<Edge> edges = new ArrayList<>();

        public Graph(int V) {
            this.V = V;
        }

        public void addEdge(int from, int to, double weight) {
            edges.add(new Edge(from, to, weight));
        }

        // Implement Chu-Liu/Edmonds algorithm here
       
        public List<Edge> findMinimumArborescence(int root) {

            
            System.out.println("Inside minimum");

            // Initialize the result arborescence
            List<Edge> arborescence = new ArrayList<>();


            System.out.println("next step");
            
        
            // Keep track of the parent of each vertex in the current tree
            int[] parent = new int[V];
            Arrays.fill(parent, -1);
            System.out.println("next next step");
            

            // Initialize minIncomingEdge array
            Edge[] minIncomingEdge = new Edge[V];
            for (int i = 0; i < V; i++) {
                minIncomingEdge[i] = null;
                
            }
            
            while (true) {
                System.out.println("inside loop");
                
                // Step 1: Find the minimum incoming edge for each vertex
                Arrays.fill(minIncomingEdge, null);
                for (Edge edge : edges) {
                    if (edge.to != root) {
                        if (minIncomingEdge[edge.to] == null || edge.weight < minIncomingEdge[edge.to].weight) {
                            minIncomingEdge[edge.to] = edge;
                        }
                    }
                }
                
                
            
                // Step 2: Detect a cycle
                boolean[] visited = new boolean[V];
                Arrays.fill(visited, false);
                int[] cycleMarker = new int[V];
                Arrays.fill(cycleMarker, -1);
                int cycleStart = -1;
            
                for (int v = 0; v < V; v++) {
                    if (visited[v] || minIncomingEdge[v] == null) {
                        continue;
                    }
            
                    int current = v;
                    while (current != -1 && cycleMarker[current] != v && !visited[current]) {
                        cycleMarker[current] = v;
                        current = minIncomingEdge[current] != null ? minIncomingEdge[current].from : -1;
                    }
            
                    if (current != -1 && cycleMarker[current] == v) {
                        cycleStart = current;
                        break;
                    }
            
                    current = v;
                    while (current != -1 && cycleMarker[current] == v) {
                        visited[current] = true;
                        current = minIncomingEdge[current] != null ? minIncomingEdge[current].from : -1;
                    }
                }
            
                if (cycleStart == -1) {
                    // No cycles found, the current tree is an arborescence
                    break;
                }
            
                // Step 3: Contract the cycle
                List<Integer> contractedVertices = new ArrayList<>();
                int current = cycleStart;
                do {
                    contractedVertices.add(current);
                    current = minIncomingEdge[current].from;
                } while (current != cycleStart);
            
                // Find the minimum weight edge entering the cycle
                Edge minEnteringEdge = null;
                for (Edge edge : edges) {
                    if (!contractedVertices.contains(edge.to)){
                        for (int v : contractedVertices) {
                            double adjustedWeight = edge.weight - (minIncomingEdge[v].weight - minIncomingEdge[cycleStart].weight);
                            if (edge.from == v && (minEnteringEdge == null || adjustedWeight < minEnteringEdge.weight)) {
                                minEnteringEdge = new Edge(edge.from, cycleStart, adjustedWeight);
                                break;
                            }
                    }

                    }
            
                    
                }
            
                // Adjust the edges of the graph by removing the cycle edges and replacing them with the minimum entering edge
                for (Iterator<Edge> iterator = edges.iterator(); iterator.hasNext(); ) {
                    Edge edge = iterator.next();
                    if (contractedVertices.contains(edge.from) || contractedVertices.contains(edge.to)) {
                        iterator.remove();
                    } else if (contractedVertices.contains(edge.to)) {
                        double adjustedWeight = edge.weight - (minIncomingEdge[edge.to].weight - minIncomingEdge[cycleStart].weight);
                        edges.add(new Edge(edge.from, cycleStart, adjustedWeight));
                        iterator.remove();
                    }
                }
                if (minEnteringEdge != null) {
                    edges.add(minEnteringEdge);
                }

                
        
                if (minEnteringEdge != null) {
                    edges.add(minEnteringEdge);
                }
                // Step 4: Adjust the minIncomingEdge array
                for (int v : contractedVertices) {
                    minIncomingEdge[v] = null;
                }
                minIncomingEdge[cycleStart] = minEnteringEdge;
            
                // Step 4: Adjust the parent references
                // for (int v : contractedVertices) {
                //     parent[v] = minEnteringEdge.from;
                // }
            }
            
        
            // Build the arborescence from the parent array
            // for (int v = 0; v < V; v++) {
            //     if (v != root && minIncomingEdge[v] != null) {
            //         arborescence.add(minIncomingEdge[v]);
            //     }
            // }

            //List<Edge> arborescence = new ArrayList<>();
            double totalWeight = 0;
            for (int v = 0; v < V; v++) {
                if (v != root && minIncomingEdge[v] != null) {
                    arborescence.add(minIncomingEdge[v]);
                    totalWeight += minIncomingEdge[v].weight;
                }
            }

            System.out.println("Arborescence --");
            for (int i = 0; i < arborescence.size(); i++) {
                Edge edge = arborescence.get(i);
                System.out.printf("%d:  (%d, %d, %.5f)\n", i + 1, edge.from, edge.to, edge.weight);
            }
            //double totalWeight = 0.0;
            for (Edge edge : arborescence) {
                totalWeight += edge.weight;
            }
            System.out.printf("Total weight: %.5f (0 ms)\n", totalWeight);
        
        
            return arborescence;
        }
        
    }

    

    public static void main(String[] args) {
        System.out.println("Inside main method");
    
        if (args.length < 1) {
            System.out.println("Please provide input filename as argument.");
            return;
        }
    
        String filename = args[0];
    
        try (Scanner scanner = new Scanner(new File(filename))) {
            int numberOfGraphs = Integer.parseInt(scanner.nextLine().split(" ")[0]);
            for (int i = 0; i < numberOfGraphs; i++) {
                // Skip the graph title and the line before edges
                scanner.nextLine();
                scanner.nextLine();

                // Read the number of vertices
                String verticesLine = scanner.nextLine();
            System.out.println("Parsing vertices line: " + verticesLine);
            int vertices = Integer.parseInt(verticesLine.split(" ")[4]);

                Graph graph = new Graph(vertices);
                
                // Read the root vertex which is always 0 for the given input
                int root = 0;

                String line;
                while (!(line = scanner.nextLine()).equals("----------------")) {
                    if(!line.contains("(") || !line.contains(")")) {
                        continue; // skip lines that don't represent edges
                    }
                    String[] parts = line.replaceAll("[(),]", "").trim().split("\\s+");
                    int from = Integer.parseInt(parts[1]);
                    int to = Integer.parseInt(parts[2]);
                    double weight = Double.parseDouble(parts[3]);
                    graph.addEdge(from, to, weight);
                }


                // Call the function to find the minimum arborescence
                List<Edge> minimumArborescence = graph.findMinimumArborescence(root);

                // Print the minimum arborescence and its total weight
                System.out.println("Graph G" + (i + 1) + ":");
                double totalWeight = 0.0;
                for (Edge edge : minimumArborescence) {
                    System.out.println(edge.from + " -> " + edge.to + " : " + edge.weight);
                    totalWeight += edge.weight;
                }
                System.out.println("Total Weight: " + totalWeight);
                System.out.println("----------------");
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        }
    }
    
}


