package com.ritesh.zstreamingquote.websocket;

public interface IwsSessionNotifier {
	
	public void notifyWsSessionOpened();
	public void notifyWsSessionClosed(boolean toTerminate);
	public void notifyWsDataMissedAfterSubscribe();
	public void notifyWsHeartBitExpired();
}
