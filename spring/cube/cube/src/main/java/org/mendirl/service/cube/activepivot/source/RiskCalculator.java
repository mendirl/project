/*
 * (C) Quartet FS 2007-2013
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.source;

import com.qfs.chunk.IArrayReader;
import com.qfs.chunk.IArrayWriter;
import com.qfs.store.record.IRecordFormat;
import com.qfs.store.transaction.ITransactionManager.IUpdateWhereProcedure;
import org.mendirl.service.cube.activepivot.SandboxActivePivotUtils;

import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <b>RiskCalculator</b>
 * <p>
 * implements for the interface IUpdateWhereProcedure.
 * <p>
 * Computes the different attributes on risks, based on attributes from the related trade and product.
 *
 * @author Quartet Financial Systems
 */
public class RiskCalculator implements IUpdateWhereProcedure {

    /**
     * Serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * The host name of the JVM
     */
    public static final String HOST_NAME = ManagementFactory.getRuntimeMXBean().getName();

    // constants used in calculation formulas
    private static final double BUMP_SIZE_50 = 0.5;
    private static final double SHIFT_OPERAND = 1.1;

    //factors used for bumpedMtmDown and bumpedMtmUp perturbation
    private static final double[] FACTORS = {.5, .2, .3, .6};

    /**
     * Index of host name column in risk store.
     */
    protected int hostnameIndex;

    /**
     * Index of delta column in risk store.
     */
    protected int deltaIndex;

    /**
     * Index of pnl delta column in risk store.
     */
    protected int pnlDeltaIndex;

    /**
     * Index of gamma column in risk store.
     */
    protected int gammaIndex;

    /**
     * Index of vega column in risk store.
     */
    protected int vegaIndex;

    /**
     * Index of pnl column in risk store.
     */
    protected int pnlIndex;

    /**
     * Index of pnl vega column in risk store.
     */
    protected int pnlVegaIndex;

    protected RiskCalculator() {
    } // Serialization

    /**
     * Constructor.
     *
     * @param hostnameIndex Index of host name column in risk store.
     * @param deltaIndex    Index of delta column in risk store.
     * @param pnlDeltaIndex Index of pnl delta column in risk store.
     * @param gammaIndex    Index of gamma column in risk store.
     * @param vegaIndex     Index of vega column in risk store.
     * @param pnlIndex      Index of pnl column in risk store.
     * @param pnlVegaIndex  Index of pnl vega column in risk store.
     */
    public RiskCalculator(
        int hostnameIndex,
        int deltaIndex, int pnlDeltaIndex,
        int gammaIndex, int vegaIndex,
        int pnlIndex, int pnlVegaIndex) {
        this.hostnameIndex = hostnameIndex;
        this.deltaIndex = deltaIndex;
        this.pnlDeltaIndex = pnlDeltaIndex;
        this.gammaIndex = gammaIndex;
        this.vegaIndex = vegaIndex;
        this.pnlIndex = pnlIndex;
        this.pnlVegaIndex = pnlVegaIndex;
    }

    protected int underlierValueIndex;
    protected int bumpedMtmUpIndex;
    protected int bumpedMtmDownIndex;

    protected int productQtyMultiplierIndex;

    @Override
    public void init(IRecordFormat selectionFormat, IRecordFormat storeFormat) {

        this.underlierValueIndex = selectionFormat.getFieldIndex("RiskToTrade/TradeToProduct/UnderlierValue");
        this.bumpedMtmUpIndex = selectionFormat.getFieldIndex("RiskToTrade/TradeToProduct/BumpedMtmUp");
        this.bumpedMtmDownIndex = selectionFormat.getFieldIndex("RiskToTrade/TradeToProduct/BumpedMtmDown");

        this.productQtyMultiplierIndex = selectionFormat.getFieldIndex("RiskToTrade/ProductQtyMultiplier");
    }

    @Override
    public void execute(IArrayReader selectedRecord, IArrayWriter recordWriter) {

        final Random random = ThreadLocalRandom.current();

        //calculate the rate change based on underlierValue and its shifted value (hard coded SHIFT_OPERAND)
        double underlierValue = selectedRecord.readDouble(this.underlierValueIndex);

        double underlierValueShifted = underlierValue * SHIFT_OPERAND;
        double rateChange = (underlierValueShifted - underlierValue) / underlierValue;

        //get the pvQtyMultiplier used in delta and pnl calculation
        double qtyMultiplier = selectedRecord.readDouble(this.productQtyMultiplierIndex);

        //get bumpedMtmUp and bumpedMtmDown : this refers to the pv after a +25% and -25% bumps
        //that's why BUMP_SIZE_50 is 50% = (+25%) - (-25%)

        double baseBumpedMtmUp = selectedRecord.readDouble(this.bumpedMtmUpIndex);
        double baseBumpedMtmDown = selectedRecord.readDouble(this.bumpedMtmDownIndex);

        double bumpedMtmUp = SandboxActivePivotUtils.round(baseBumpedMtmUp + baseBumpedMtmUp * FACTORS[random.nextInt(FACTORS.length)], 2);
        double bumpedMtmDown = SandboxActivePivotUtils.round(baseBumpedMtmDown + baseBumpedMtmDown * FACTORS[random.nextInt(FACTORS.length)], 2);

        //calculating and adding new measures that depend on external MarketData (rateChange in our case)

        double difference = ((bumpedMtmUp - bumpedMtmDown) / BUMP_SIZE_50);

        double delta = qtyMultiplier * difference;
        double pnlDelta = rateChange * delta;

        /*
         * theta, gamma, vega, rho, pnlVega and pnl calculation
         * this formulas do not match the reality the aim here is to have different values for each measure
         * in a real project these values can be calculated in the calculator or retrieved form an external pricer
         *
         * gamma = delta * random[-.1 , +.1]
         * vega = delta * random[-1 , +1]
         * theta = pv * 3 / 365
         * rho = -pv * 1/150
         */
        double gamma = delta * SandboxActivePivotUtils.nextDouble(-0.1, 0.1, random);
        double vega = delta * SandboxActivePivotUtils.nextDouble(-1, 1, random);
        double pnlVega = vega * 0.01;
        double pnl = pnlVega + pnlDelta;

        recordWriter.write(this.hostnameIndex, HOST_NAME);

        recordWriter.writeDouble(this.deltaIndex, delta);
        recordWriter.writeDouble(this.pnlDeltaIndex, pnlDelta);
        recordWriter.writeDouble(this.gammaIndex, gamma);
        recordWriter.writeDouble(this.vegaIndex, vega);
        recordWriter.writeDouble(this.pnlVegaIndex, pnlVega);
        recordWriter.writeDouble(this.pnlIndex, pnl);
        recordWriter.writeDouble(this.gammaIndex, gamma);

    }

    /**
     * Sets the hostnameIndex
     *
     * @param hostnameIndex The hostnameIndex to set
     */
    public void setHostnameIndex(int hostnameIndex) {
        this.hostnameIndex = hostnameIndex;
    }

    /**
     * Sets the deltaIndex
     *
     * @param deltaIndex The deltaIndex to set
     */
    public void setDeltaIndex(int deltaIndex) {
        this.deltaIndex = deltaIndex;
    }

    /**
     * Sets the pnlDeltaIndex
     *
     * @param pnlDeltaIndex The pnlDeltaIndex to set
     */
    public void setPnlDeltaIndex(int pnlDeltaIndex) {
        this.pnlDeltaIndex = pnlDeltaIndex;
    }

    /**
     * Sets the vegaIndex
     *
     * @param vegaIndex The vegaIndex to set
     */
    public void setVegaIndex(int vegaIndex) {
        this.vegaIndex = vegaIndex;
    }

    /**
     * Sets the pnlIndex
     *
     * @param pnlIndex The pnlIndex to set
     */
    public void setPnlIndex(int pnlIndex) {
        this.pnlIndex = pnlIndex;
    }

    /**
     * Sets the pnlVegaIndex
     *
     * @param pnlVegaIndex The pnlVegaIndex to set
     */
    public void setPnlVegaIndex(int pnlVegaIndex) {
        this.pnlVegaIndex = pnlVegaIndex;
    }

    /**
     * Sets the gammaIndex
     *
     * @param gammaIndex The gammaIndex to set
     */
    public void setGammaIndex(int gammaIndex) {
        this.gammaIndex = gammaIndex;
    }

}
