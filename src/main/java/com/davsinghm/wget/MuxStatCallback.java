package com.davsinghm.wget;

public interface MuxStatCallback {

    void onStatisticsUpdated(int time, int totalTime);

    void onMuxFinished(long fileSize);

}
