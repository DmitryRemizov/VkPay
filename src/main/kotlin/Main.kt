const val TotalInRouble = 100.0
const val DayLimitCards = 150_000.0 * TotalInRouble
const val DayLimitVkPay = 15_000.0 * TotalInRouble
const val PercentVisaMir = 0.0075
const val MinTaxVisaMir = 35.0 * TotalInRouble
const val MaxSumPerMonthMastercardMaestro = 75_000.0 * TotalInRouble
const val PercentMastercardMaestro = 0.006
const val FixTaxMastercardMaestro = 20.0 * TotalInRouble

enum class CardOrAccountType { VKPay, MasterCardMaestro, VisaMir }

/*
Для упрощения проверяем только лимит переводов в сутки.
Причем для карт - по всем картам в сумме, а не по отдельности.
 */
fun main(args: Array<String>) {
    var totalSumInMonthCards = 0.0
    var totalSumInMonthVKPay = 0.0
    var limitExceeded = false
    var errorText = ""
    while (true) {
        print("-= Тип карты/счета =-\n1) VK Pay\n2) MasterCard/Maestro\n3) Visa/МИР\n0) Выход\nВведите номер: ")
        val accountType = readln().toInt();
        if (accountType == 0) {
            println("Программа завершена.")
            break
        }
        val selectedCardOrAccount = when (accountType) {
            1 -> CardOrAccountType.VKPay
            2 -> CardOrAccountType.MasterCardMaestro
            3 -> CardOrAccountType.VisaMir
            else -> CardOrAccountType.VKPay
        }
        print("Введите сумму перевода (руб.): ")
        val transferSum = readln().toDouble() * TotalInRouble
        if (selectedCardOrAccount == CardOrAccountType.VKPay) {
            totalSumInMonthVKPay += transferSum
            val totalSumInMonthVKPayRub = totalSumInMonthVKPay / TotalInRouble
            val dayLimitVKPayRub = DayLimitVkPay / TotalInRouble
            limitExceeded = totalSumInMonthVKPay > DayLimitCards
            errorText =
                "Невозможно сделать перевод со счета VK Pay: исчерпан лимит на сутки.\nС учетом запрошенной суммы перевода сумма переводов за сутки составит $totalSumInMonthVKPayRub руб. Лимит: $dayLimitVKPayRub руб.\n\n"
            if (limitExceeded) totalSumInMonthVKPay -= transferSum
        } else {
            totalSumInMonthCards += transferSum
            val totalSumInMonthCardsRub = totalSumInMonthCards / TotalInRouble
            val dayLimitCardsyRub = DayLimitCards / TotalInRouble
            limitExceeded = totalSumInMonthCards > DayLimitCards
            errorText =
                "Невозможно сделать перевод с карты: исчерпан лимит на сутки.\nС учетом запрошенной суммы перевода сумма переводов за сутки составит $totalSumInMonthCardsRub руб. Лимит: $dayLimitCardsyRub руб.\n\n"
            if (limitExceeded) totalSumInMonthCards -= transferSum
        }
        if (!limitExceeded) {
            val taxSum = if (selectedCardOrAccount == CardOrAccountType.VKPay)
                transferFeeCalculation(transferSum, totalSumInMonthVKPay)
            else
                transferFeeCalculation(transferSum, totalSumInMonthCards, selectedCardOrAccount)

            val taxSumRub = taxSum / TotalInRouble
            println("Комиссия за перевод составит $taxSumRub руб.\n\n")
        } else {
            println(errorText)
        }
    }
}

fun transferFeeCalculation(
    transferSum: Double,
    totalSum: Double = 0.0,
    accountType: CardOrAccountType = CardOrAccountType.VKPay
) = when (accountType) {
    CardOrAccountType.MasterCardMaestro ->
        if (totalSum > MaxSumPerMonthMastercardMaestro) transferSum * PercentMastercardMaestro + FixTaxMastercardMaestro else 0.0

    CardOrAccountType.VisaMir ->
        if (transferSum * PercentVisaMir <= MinTaxVisaMir) MinTaxVisaMir else transferSum * PercentVisaMir

    else -> 0.0
}