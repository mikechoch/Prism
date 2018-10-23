package com.mikechoch.prism.callback.check;

public interface OnMaintenanceCheckCallback {
    void onStatusActive();
    void onStatusUnderMaintenance(String message);
    void onAppVersionTooOld();
    void onStatusCheckFailed(Exception e);
}
