object Utils {

    fun sleep(milliseconds: Int) {
        try {
            Thread.sleep(milliseconds.toLong())
        } catch (e: Exception) {
        }

    }

    fun fatalError(e: Exception) {
        e.printStackTrace()
        System.exit(-1)
    }

    fun fatalError(e: String) {
        System.err.println(e)
        System.exit(-1)
    }
}
