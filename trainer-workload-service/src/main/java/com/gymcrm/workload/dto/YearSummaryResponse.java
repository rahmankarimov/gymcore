package com.gymcrm.workload.dto;

import java.util.List;

public record YearSummaryResponse(int year, List<MonthSummaryResponse> months) {
}
