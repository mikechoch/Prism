package com.mikechoch.prism.fire.callback;

public interface OnMaintenanceCheckCallback {
    void onStatusActive();
    void onStatusUnderMaintenance(String message);
    void onStatusCheckFailed(Exception e);
}
