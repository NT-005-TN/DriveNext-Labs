package ru.mtuci.drivenext.domain
import java.time.LocalDate
import java.time.temporal.ChronoUnit
data class RentalQuote(val days:Long,val rental:Long,val insurance:Long,val deposit:Int) {
    val total:Long get()=rental+insurance
    companion object {
        fun calculate(start:LocalDate,end:LocalDate,price:Int,insurance:Int,deposit:Int,today:LocalDate=LocalDate.now()):RentalQuote {
            require(!start.isBefore(today) && !end.isBefore(start)) { "Проверьте порядок дат и дату начала." }
            val days=ChronoUnit.DAYS.between(start,end)+1
            require(days in 1..366 && price>0 && insurance>=0 && deposit>=0) { "Некорректный срок или тариф." }
            return RentalQuote(days,days*price,days*insurance,deposit)
        }
    }
}
