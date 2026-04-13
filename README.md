## Graph Visualizer

> Personal copy of a team project from SPbU.
> Original repository: [graphs-graphs-team-7](https://github.com/spbu-coding-2024/graphs-graphs-team-7) - — developed solely by me.

MVVM desktop application for graph visualization, built with Kotlin and Compose Desktop.

## Local start
1. Clone the repository:
 ```git clone git@github.com:VladimirKhmelev/Graphs-on-Kotlin.git ```
2. Go to the project directory:
 ```cd Graphs-on-Kotlin```
3. Build the project with Gradle:
```./gradlew build```
4. Run application:
```./gradlew run```
5. Also run tests:
```./gradlew test```

## Localization
- Russian 🇷🇺
- English 🇬🇧

## Usage

<img width="1280" height="747" alt="image" src="https://github.com/user-attachments/assets/6269023e-aaae-47e9-8d13-722322c8bf71" />
After launching, you will see a white canvas.

You can view it by clicking on "Help".

To create a graph, you can click on the "Generate" button and select the number of vertices.

To connect vertices, you can click on "Merge Vertices" and on the "V" keyboard, and then click on two vertices.

If you want to create an edge with a certain weight, then click on "Add Edge", then select the vertices you need and write the weight.

There are settings for Dijkstra's algorithm:
1. Click on "Set Start".
2. Click on a vertex.
3. Click on "Set End"
4. Click on a vertex.
5. Click on "Algorithms" - "Dijkstra's algorithm" - 
The shortest path and the route weight will be displayed at the top.


## Algorithms
+ [Dijkstra's algorithm](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm) - finds the shortest path in a weighted graph.
+ [Kosaraju's algorithm](https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm) - detects strongly connected components.
+ [ForceAtlas2 algorithm](https://github.com/gephi/gephi/wiki/Force-Atlas-2) - an algorithm for laying out a graph on a plane based on the forces of attraction and attraction.

## Working with a graph
+ JSON - The application implements convenient work with JSON files, which allows users to save and load graphs with all their properties preserved.
Each graph is saved with all vertices, edges, weights and additional parameters, such as vertex positions on the plane. This ensures accurate restoration of the graph state when saving/loading.
+ CSV - tabular format support is implemented: easy editing in Excel/Google tables.

## Based on 
+ Jetpack Compose 1.6.0
+ Gradle 8.13
+ Jacoco
+ JUnit5
+ Kotlin 1.9.22

## License
+ [License](https://github.com/VladimirKhmelev/Graphs-on-Kotlin/blob/main/LICENSE) - This project is licensed under the MIT license.

## Contact 
+ [Vladimir Khmelev](https://t.me/khmelevvova)
