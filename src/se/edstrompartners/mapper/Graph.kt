package se.edstrompartners.mapper

open class Graph<ND, ED> {
    private inner class Edge(val start: Node, val end: Node, val data: ED) {

        override fun toString(): String = "($start -> $end)"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Graph<*, *>.Edge) {
                return false
            }
            return ((this.start == other.start
                && this.end == other.end)
                || (this.start == other.end
                && this.end == other.start))

        }

        override fun hashCode(): Int {
            return start.hashCode() + end.hashCode()
        }

    }

    private inner class Node(val data: ND) {
        val edges = mutableSetOf<Edge>()
        val neighbors: Set<Node>
            get() = edges.map { if (this == it.start) it.end else it.start }.toSet()

        fun edgeTo(other: Node?): Edge? {
            return edges.firstOrNull { it.start == other || it.end == other }
        }

        fun addEdge(that: Node, data: ED): Edge {
            val edge = Edge(this, that, data)
            edges.add(edge)
            that.edges.add(edge)
            return edge
        }

        override fun toString(): String = "$data"
    }

    private val _nodes = hashMapOf<ND, Node>()
    val nodes: Set<ND> get() = _nodes.keys

    private val _edges = hashMapOf<ED, Edge>()
    val edges: Set<ED> get() = _edges.keys

    fun addNode(data: ND): ND {
        _nodes.put(data, Node(data))
        return data
    }

    fun getNodes(data: ED): List<ND> {
        val edge = _edges[data]!!
        return listOf(edge.start.data, edge.end.data)
    }

    fun getEdges(data: ND): List<ED> {
        val node = _nodes[data]!!
        return node.edges.map { it.data }
    }

    fun getNeighbors(data: ND): List<ND> {

        val node = _nodes[data]!!
        return node.neighbors.map { it.data }
    }

    operator fun contains(o: ND): Boolean {
        return o in _nodes
    }


    fun addEdge(start: ND, end: ND, data: ED) {
        if (start !in _nodes || end !in _nodes) {
            throw IllegalArgumentException("Nodes not in graph")
        }
        val startNode = _nodes[start]!!
        val endNode = _nodes[end]!!
        val edge = startNode.addEdge(endNode, data)
        _edges.put(data, edge)
    }

    override fun toString(): String =
        "Graph[${_nodes.map { (data, node) ->
            "$data -> ${node.neighbors}"
        }}]"

//        "Graph[${_nodes.values.map { "$it -> ${it.neighbors}" }}]"

    fun pathfind(src: ND, dst: ND): List<ED>? {
        val stack = mutableListOf(src)
        val visited = mutableSetOf(src)
        val cameFrom = HashMap<ND, ND>()

        var cur: ND
        do {
            cur = stack.removeAt(stack.size - 1)
            if (cur == dst) {
                val path = mutableListOf<ED>()
                var prev0 = cur
                var prev1 = cameFrom[cur]
                while (prev1 != null) {
                    path.add(isAdjacent(prev0, prev1)!!)
                    prev1 = prev0
                    prev0 = cameFrom[prev0]!!
                }
                return path.reversed()
            }
            _nodes[cur]!!.neighbors.map { it.data }.filter { it !in visited }.forEach {
                stack.add(it)
                visited.add(it)
                cameFrom.put(it, cur)
            }
        } while (stack.isNotEmpty())
        return null
    }

    private fun isAdjacent(a: ND, b: ND): ED? {
        return _nodes[a]?.edgeTo(_nodes[b])?.data
    }

    fun <NND> mapNodes(mapper : (ND) -> NND) : Graph<NND, ED> {
        val res = Graph<NND, ED>()
        val mapping = hashMapOf<ND, NND>()
        nodes.forEach { node ->
            val mapped = mapper(node)
            mapping.put(node, mapped)
            res.addNode(mapped)
        }

        _edges.values.forEach { res.addEdge(mapping[it.start.data]!!, mapping[it.end.data]!!, it.data) }

        return res
    }
}