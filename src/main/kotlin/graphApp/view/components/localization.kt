package graphApp.view.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object Localization {
    private var currentLanguage by mutableStateOf("en")

    private val translations = mapOf(
        "en" to mapOf(
            //MainView
            "app_title" to "Graph Visualizer",
            "menu_new_window" to "New Window",
            "exit" to "Exit",
            "add" to "Add",
            "menu_exit" to "Exit",
            "button_open" to "Open file",
            "button_save" to "Save",
            "button_undo" to "Undo (Ctrl+Z)",
            "button_redo" to "Redo (Ctrl+Y)",
            "button_help" to "Help",
            "dialog_edit_edge" to "Edit Edge",
            "dialog_weight" to "Weight",
            "dialog_directed" to "Directed edge",
            "dialog_save" to "Save",
            "dialog_cancel" to "Cancel",
            "help_title" to "Controls",
            "select_algo" to "Select Algorithm",
            "cancel" to "Cancel",
            "start_vert" to "Select start vertex",
            "end_vert" to "Select end vertex",
            "v_mode_first" to "Connection mode: select the first vertex (Esc - cancel)",
            "v_mode_second" to "Connection mode: select the second vertex (Esc - cancel)",
            "v_mode_first_choose" to "Connection mode: select the first vertex",
            "shortest_path_result" to "Shortest path: %d vertices, weight: %s",
            "strong_components_result" to "Found strong connected components: %d",
            "dijkstra_vertices_not_selected" to "Select start and end vertices for Dijkstra",
            "help_v_mode" to "• V: activate vertex connection mode",
            "help_esc" to "• Esc: cancels the current action",
            "help_add_vertex" to "• Add Vertex or LMB + RMB: add a vertex",
            "help_move_vertex" to "• LMB on a vertex: move vertices",
            "help_zoom" to "• Wheel: zoom",
            "help_undo_redo" to "• Ctrl+Z/Ctrl+Y and the buttons below: undo/redo actions",

            //Filedialogs
            "change_format" to "Change format:",
            "save_format" to "Save the graph in the format:",
            "save_graph" to "Save_graph",
            "open_graph" to "Open graph",
            "open_graph_format" to "Open graph in the format:",
            "open" to "Open",

            //AlgorithmType
            "dij" to "Dijkstra's algorithm",
            "for" to "ForceAtlas2",
            "kos" to "searching for components of strong connectivity",

            //controlpanel
            "generate" to "Generate",
            "merge_vert" to "Merge Vertices",
            "clear" to "Clear Graph",
            "add_v" to "Add Vertex",
            "add_e" to "Add Edge",
            "algo" to "Algorithms",
            "help" to "Help",
            "dj" to "Dijkstra Settings",
            "set_start_button" to "Set Start: %s",
            "set_end_button" to "Set End: %s",
            "not_selected" to "Not selected",

            //GenerateGraphDialog
            "gen_graph" to "Generate Graph",
            "graph_type" to "Graph Type:",
            "ver_cnt" to "Vertex count",
            "edg_prob" to "Edge Probability (0.0 - 1.0)",
            "min_wei" to "Min Weight",
            "max_wei" to "Max Weight",
            "gen" to "Generate",
            "graph_type_tree" to "Tree",
            "graph_type_random" to "Random",
            "graph_type_weighted_graph" to "Weighted Graph",

            //GraphViewModel
            "layout_suc" to "layout applied",
            "path_not_found" to "Path not found",
            "empty_graph" to "Graph is empty",

            // SettingsDialog
            "customize" to "Customize",
            "customize_settings" to "Customize Graph",
            "vertices" to "Vertices",
            "edges" to "Edges",
            "arrow_settings" to "Arrow Settings",
            "arrow_color" to "Arrow Color",
            "arrow_size" to "Arrow Size",
            "arrow_thickness" to "Arrow Thickness",
            "arrows" to "Arrows",
            "vertex_color" to "Vertex Color",
            "vertex_radius" to "Vertex Radius",
            "edge_color" to "Edge Color",
            "edge_width" to "Edge Width",
            "animated_edges" to "Animated Edges",
            "red" to "Red",
            "green" to "Green",
            "blue" to "Blue",
            "save" to "Save",
            "animation_speed" to "Animation Speed",
            "edge_color" to "Edge Color",
            "edge_width" to "Edge Width",
            "animation_speed" to "Animation Speed",
            "current_speed" to "Current: %.1fx",
            "slow" to "Slow",
            "fast" to "Fast",

            // DatabaseDialog
            "db_title" to "Database",
            "db_save" to "Save",
            "db_load" to "Load",
            "db_name" to "Graph name",
            "db_name_empty" to "Please enter a name",
            "db_empty" to "No saved graphs",
            "db_delete" to "Delete",
            "button_database" to "Database"
        ),

        "ru" to mapOf(
            //GraphViewModel
            "layout_suc" to "Раскладка графа применена",
            "path_not_found" to "Путь не найден",
            "empty_graph" to "Пустой граф",

            //MainView
            "app_title" to "Визуализатор графов",
            "v_mode_first" to "Режим соединения: выберите первую вершину (Esc - отмена)",
            "v_mode_second" to "Режим соединения: выберите вторую вершину (Esc - отмена)",
            "v_mode_first_choose" to "Режим соединения: выберите первую вершину",
            "shortest_path_result" to "Кратчайший путь: %d вершин, вес: %s",
            "strong_components_result" to "Найдено компонент сильной связности: %d",
            "dijkstra_vertices_not_selected" to "Выберите начальную и конечную вершины для алгоритма Дейкстры",
            "help_v_mode" to "• V: активирует режим соединения вершин",
            "help_esc" to "• Esc: отменяет текущее действие",
            "help_add_vertex" to "• Add Vertex или ЛКМ + ПКМ: добавить вершину",
            "help_move_vertex" to "• ЛКМ по вершине: перемещение вершин",
            "help_zoom" to "• Колёсико: масштабирование",
            "help_undo_redo" to "• Ctrl+Z/Ctrl+Y и кнопки снизу: отмена/повтор действий",
            "menu_new_window" to "Новое окно",
            "menu_exit" to "Выход",
            "add" to "Добавить",
            "exit" to "Выход",
            "button_open" to "Открыть файл",
            "button_save" to "Сохранить",
            "button_undo" to "Отменить (Ctrl+Z)",
            "button_redo" to "Повторить (Ctrl+Y)",
            "button_help" to "Помощь",
            "dialog_edit_edge" to "Редактировать ребро",
            "dialog_weight" to "Вес",
            "dialog_directed" to "Направленное ребро",
            "dialog_save" to "Сохранить",
            "dialog_cancel" to "Отмена",
            "help_title" to "Управление",
            "select_algo" to "Выбор алгоритма",
            "cancel" to "Отмена",
            "start_vert" to "Выберите начальную вершину",
            "end_vert" to "Выберите конечную вершину",
            "d_mode_hint" to "Режим удаления: кликните на вершину (Esc - отмена)",

            //GenerateGraphDialog
            "gen_graph" to "Создать случайный граф",
            "graph_type" to "Тип графа",
            "ver_cnt" to "Количество вершин",
            "edg_prob" to "Вероятность рёбер (0.0 - 1.0)",
            "min_wei" to "Минимальный вес",
            "max_wei" to "Максимальный вес",
            "gen" to "Создать",
            "graph_type_tree" to "Дерево",
            "graph_type_random" to "Случайный",
            "graph_type_weighted_graph" to "Взвешенный граф",


            //Filedialogs
            "change_format" to "Выберите формат:",
            "save_format" to "Сохранить граф в формате:",
            "save_graph" to "Сохранить граф",
            "open_graph" to "Открыть граф",
            "open_graph_format" to "Открыть граф в формате:",
            "open" to "Открыть",

            //AlgorithmType
            "dij" to "Алгоритм Дейкстры",
            "for" to "Алгоритм раскладки",
            "kos" to "Поиск компонент сильной связности",

            // Controlpanel
            "generate" to "Случайная генерация",
            "merge_vert" to "Соединение вершин",
            "clear" to "Очистить холст",
            "add_v" to "Добавить вершину",
            "add_e" to "Добавить ребро",
            "algo" to "Алгоритмы",
            "help" to "Управление",
            "dj" to "Настройки алгоритма Дейкстры",
            "set_start_button" to "Начало: %s",
            "set_end_button" to "Конец: %s",
            "not_selected" to "Не выбрано",

            // SettingsDialog
            "customize" to "Кастомизация",
            "customize_settings" to "Настройки графа",
            "vertices" to "Вершины",
            "edges" to "Рёбра",
            "arrow_settings" to "Настройки стрелок",
            "arrow_color" to "Цвет стрелок",
            "arrow_size" to "Размер стрелок",
            "arrow_thickness" to "Толщина стрелок",
            "arrows" to "Стрелки",
            "vertex_color" to "Цвет вершин",
            "vertex_radius" to "Радиус вершин",
            "edge_color" to "Цвет рёбер",
            "edge_width" to "Ширина рёбер",
            "animated_edges" to "Анимированные рёбра",
            "red" to "Красный",
            "green" to "Зелёный",
            "blue" to "Синий",
            "save" to "Сохранить",
            "animation_speed" to "Скорость анимации",
            "animated_edges" to "Анимированные рёбра",
            "edge_color" to "Цвет рёбер",
            "edge_width" to "Ширина рёбер",
            "animation_speed" to "Скорость анимации",
            "current_speed" to "Текущая: %.1fх",
            "slow" to "Медленно",
            "fast" to "Быстро",

            // DatabaseDialog
            "db_title" to "База данных",
            "db_save" to "Сохранить",
            "db_load" to "Загрузить",
            "db_name" to "Название графа",
            "db_name_empty" to "Введите название",
            "db_empty" to "Нет сохранённых графов",
            "db_delete" to "Удалить",
            "button_database" to "База данных"
        )
    )

    fun setLanguage(lang: String) {
        if (translations.containsKey(lang)) {
            currentLanguage = lang
        }
    }

    fun tr(key: String): String {
        return translations[currentLanguage]?.get(key) ?: translations["en"]?.get(key) ?: key
    }
}