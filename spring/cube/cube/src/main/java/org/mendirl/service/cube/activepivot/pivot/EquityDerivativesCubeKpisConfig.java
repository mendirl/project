/*
 * (C) Quartet FS 2017
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.pivot;

import com.quartetfs.biz.pivot.definitions.IKpiDescription;
import com.quartetfs.biz.pivot.definitions.impl.*;

/**
 * Definitions of the KPIs used in {@link EquityDerivativesCubeConfig}.
 *
 * @author ActiveViam
 */
public class EquityDerivativesCubeKpisConfig {

    /**
     * A kpi that monitors pnl.SUM.
     *
     * @return the kpi description.
     */
    public static IKpiDescription kpiPnlMonitoring() {
        return KpiDescription.builder()
            .withName("PnL Monitoring")
            .withValue(new KpiValueDescription("[Measures].[pnl.SUM]"))
            .withDescription("Pnl Monitoring using existing measures")
            .withGoal(new KpiGoalDescription("[Measures].[PnL Limit]"))
            .withStatus(new KpiStatusDescription("[Measures].[PnL Limit Status]", "Shapes"))
            .build();
    }

    /**
     * This KPI is the exact same KPI as "PnL Monitoring" but instead of post-processed measures defining
     * goal and status, this uses MDX formula.
     *
     * @return the kpi description.
     */
    public static IKpiDescription kpiPnlMonitoringWithMdx() {
        return KpiDescription.builder()
            .withName("PnL Monitoring (MDX)")
            .withValue(new KpiValueDescription("[Measures].[pnl.SUM]"))
            .withCaption("PnL Monitoring (MDX)")
            .withGoal(new KpiGoalDescription("0", "'#'"))
            // To address the value and goal defined by this KPI, it is possible to use the MDX functions
            // KpiValue and KpiGoal. Note that KpiStatus and KpiTrend also exist.
            .withStatus(new KpiStatusDescription("IIF(KpiValue(\"PnL Monitoring\") >= KpiGoal(\"PnL Monitoring\"), 1, -1)", "Shapes"))
            // Additionally, it is possible to use Epoch dimension to easily compute the trend here.
            .withTrend(new KpiTrendDescription(
                "	Case"
                    + "		When [Epoch].[Epoch].CurrentMember.level.ordinal = 0"
                    + "			Then Case"
                    + "			When KpiValue(\"PnL Monitoring (MDX)\") - (KpiValue(\"PnL Monitoring (MDX)\"), [Epoch].[Epoch].CurrentMember.firstChild.nextMember) > 0"
                    + "				Then 1"
                    + "			When KpiValue(\"PnL Monitoring (MDX)\") - (KpiValue(\"PnL Monitoring (MDX)\"), [Epoch].[Epoch].CurrentMember.firstChild.nextMember) = 0"
                    + "				Then 0"
                    + "			Else -1"
                    + "			End"
                    + "		Else"
                    + "			Case"
                    + "			When KpiValue(\"PnL Monitoring (MDX)\") - (KpiValue(\"PnL Monitoring (MDX)\"), [Epoch].[Epoch].CurrentMember.nextMember) > 0"
                    + "				Then 1"
                    + "			When KpiValue(\"PnL Monitoring (MDX)\") - (KpiValue(\"PnL Monitoring (MDX)\"), [Epoch].[Epoch].CurrentMember.nextMember) = 0"
                    + "				Then 0"
                    + "			Else -1"
                    + "			End"
                    + "		End",
                null))
            .build();
    }

    /**
     * This KPI illustrates the usage of the LookUp function. In the previous KPI "PnL Monitoring (MDX)",
     * the goal is a hard-coded constant.
     * ActiveMonitor project brings a Repository to ActivePivot server, as well as a LookUp function. Consider
     * LookUp as a way to access parameters, configurable for any tuple, and that can dynamically change.
     * In the following, we use a parameter named "pnl_sum_goal" as the goal of the KPI.
     *
     * @return the kpi description
     */
    public static IKpiDescription kpiPnlMonitoringWithLookup() {
        return KpiDescription.builder()
            .withName("PnL Monitoring (LookUp)")
            .withValue(new KpiValueDescription("[Measures].[pnl.SUM]"))
            .withCaption("PnL Monitoring (LookUp)")
            .withGoal(new KpiGoalDescription("lookup(\"pnl_sum_goal\")", "'#,###'"))
            // This status also shows that KPI expressions, such as value or goal can be referenced
            // by their generated measure name
            .withStatus(new KpiStatusDescription(
                "Case"
                    + " When IsEmpty([Measures].[PnL Monitoring (LookUp) Goal])"
                    + " Then NULL // No goal => no status \n"
                    + " When [Measures].[pnl.SUM] > [Measures].[PnL Monitoring (LookUp) Goal]"
                    + " Then 1"
                    + " Else -1"
                    + " End",
                "Shapes"))
            .build();
    }

    /**
     * The following demonstrates a KPI with a smoothing period.
     * Smooth status returns the lowest status value in a given period of time.
     * The smoothing is interesting for cases where data can be partially committed into the
     * pivot. For example, if it takes 15s to receive all data but that data are published
     * as soon as they arrive, the KPI may breach since only part of the data are considered.
     * By smoothing the status within a time window of 15s, we avoid receiving false positive breaches.
     *
     * @return the kpi description.
     */
    public static IKpiDescription kpiSmoothedPnl() {
        return KpiDescription.builder()
            .withName("Smoothed PnL")
            .withValue(new KpiValueDescription("[Measures].[pnl.SUM]"))
            .withDescription("PnL monitoring with a smoothing period of 15s")
            .withDuring("15s")
            .withGoal(new KpiGoalDescription("[Measures].[PnL Limit]"))
            .withStatus(new KpiStatusDescription("[Measures].[PnL Limit Status]", "Shapes"))
            .build();
    }

    /**
     * This KPI illustrates monitoring of data within a range, defined with upper and lower boundaries.
     * The range bounds are both defined using parameters.
     *
     * @return the kpi description.
     */
    public static IKpiDescription kpiTunnelCountMonitoring() {
        return KpiDescription.builder()
            .withName("Tunnel Count Monitoring")
            .withValue(new KpiValueDescription("[Measures].[contributors.COUNT]"))
            .withStatus(new KpiStatusDescription(
                "Case"
                    + "	When IsEmpty(lookup(\"lower_goal\")) OR IsEmpty(lookup(\"upper_goal\"))"
                    + "	Then NULL"
                    + "	When [Measures].[contributors.COUNT] < lookup(\"lower_goal\") - 0.1 * ABS(lookup(\"lower_goal\"))"
                    + "	Then -1"
                    + "	When [Measures].[contributors.COUNT] < lookup(\"lower_goal\")"
                    + "	Then 0"
                    + "	When [Measures].[contributors.COUNT] < lookup(\"upper_goal\")"
                    + "	Then 1"
                    + "	When [Measures].[contributors.COUNT] < lookup(\"upper_goal\") + 0.04 * ABS(lookup(\"upper_goal\"))"
                    + "	Then 0"
                    + "	Else -1"
                    + "	End",
                "Shapes"))
            .build();
    }

    /**
     * This kpi illustrates the usage of default value with LookUp.
     * It checks if the PnL value matches the sum of its sensitivities.
     * The default here for every location is 100%.
     *
     * @return the kpi description.
     */
    public static IKpiDescription kpiPnlSensitivity() {
        return KpiDescription.builder()
            .withName("Pnl Sensitivity")
            .withValue(new KpiValueDescription(
                "Abs(Divide("
                    + "		[Measures].[pnlDelta.SUM] + [Measures].[pnlVega.SUM],"
                    + "		[Measures].[pnl.SUM],"
                    + "		NULL"
                    + "	))",
                null))
            .withGoal(new KpiGoalDescription("LookUp(\"pnl_sensitivity\", 1)"))
            .withStatus(new KpiStatusDescription(
                "Case"
                    + "	When [Measures].[Pnl Sensitivity Value] > [Measures].[Pnl Sensitivity Goal]"
                    + "		Then 1"
                    + "	When [Measures].[Pnl Sensitivity Value] > 0.90 * [Measures].[Pnl Sensitivity Goal]"
                    + "		Then 0"
                    + "	Else -1"
                    + "	End",
                "Shapes"))
            .build();
    }

    /**
     * This KPI shows that LookUp and parameters can be called for other usages than goal values.
     * In this example, we also use a parameter to define a dynamic warning threshold for the limit.
     * It generates alerts on the members that contributes too largely to their aggregated parent.
     *
     * @return the kpi description.
     */
    public static IKpiDescription kpiDataContribution() {
        return KpiDescription.builder()
            .withName("Data contribution")
            .withValue(new KpiValueDescription(
                "IIF("
                    + "		NOT IsEmpty([Underlyings].[Underlyings].CurrentMember.Parent),"
                    + "		[Measures].[contributors.COUNT] / ([Measures].[contributors.COUNT], [Underlyings].[Underlyings].CurrentMember.Parent),"
                    + "		NULL"
                    + "	)",
                "'#.###'"))
            .withGoal(new KpiGoalDescription("Divide(1, [Underlyings].[Underlyings].CurrentMember.Siblings.Count, NULL)", "'#.###'"))
            .withStatus(new KpiStatusDescription(
                "Case"
                    + "	When IsEmpty([Measures].[Data contribution Value]) OR IsEmpty([Measures].[Data contribution Goal])"
                    + "		Then NULL"
                    + "	When [Measures].[Data contribution Value] >= [Measures].[Data contribution Goal] * (1 + lookup(\"warning_threshold\", 0.05))"
                    + "		Then -1"
                    + "	When [Measures].[Data contribution Value] > [Measures].[Data contribution Goal]"
                    + "		Then 0"
                    + "	Else 1"
                    + "	End",
                "Shapes"))
            .build();
    }

}
