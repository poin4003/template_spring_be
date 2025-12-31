package com.template.app.core.sync;

public interface SyncableDataService {
    
    String getSyncType();

    void syncToDatabase();

}
