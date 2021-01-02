package org.mendirl.service.cube.activepivot.model;

public class Rate {
    protected String currency;
    protected String targetCurrency;
    protected double rate;

    public Rate(String fr, String to, double rate) {
        this.currency = fr;
        this.targetCurrency = to;
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return (currency + "-->" + targetCurrency + ":" + rate);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        long temp;
        temp = Double.doubleToLongBits(rate);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((targetCurrency == null) ? 0 : targetCurrency.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Rate other = (Rate) obj;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (Double.doubleToLongBits(rate) != Double.doubleToLongBits(other.rate))
            return false;
        if (targetCurrency == null) {
            if (other.targetCurrency != null)
                return false;
        } else if (!targetCurrency.equals(other.targetCurrency))
            return false;
        return true;
    }

}
