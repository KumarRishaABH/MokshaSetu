package MoonshotApp.MokshaSetu.data

private const val RUPEE = "₹"

fun formatRupees(amount: Long): String {
    val sign = if (amount < 0) "-" else ""
    val digits = (if (amount < 0) -amount else amount).toString()
    if (digits.length <= 3) return "$sign$RUPEE$digits"

    val lastThree = digits.takeLast(3)
    val rest = digits.dropLast(3)
    val grouped = StringBuilder()
    var end = rest.length
    while (end > 0) {
        val start = (end - 2).coerceAtLeast(0)
        if (grouped.isNotEmpty()) grouped.insert(0, ',')
        grouped.insert(0, rest.substring(start, end))
        end = start
    }
    return "$sign$RUPEE$grouped,$lastThree"
}
