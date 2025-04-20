package api.papaer.net.dtos.graphics;

import java.math.BigDecimal;

public class TotalShoppingByMonthDto {

    private String month;
    private BigDecimal total;

    public  TotalShoppingByMonthDto(){

    }

    public TotalShoppingByMonthDto(String month, BigDecimal total) {
        this.month = month;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
