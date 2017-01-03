package com.pedro.easyyt.domain.interactor.yasea;

import com.github.faucamp.simplertmp.RtmpHandler;
import java.io.IOException;
import java.net.SocketException;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

/**
 * Created by pedro on 3/01/17.
 */

public class YaseaWrapper implements RtmpHandler.RtmpListener,
    SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener{

  private SrsPublisher srsPublisher;

  public SrsPublisher getSrsPublisher() {
    return srsPublisher;
  }

  public void setSrsPublisher(SrsPublisher srsPublisher) {
    this.srsPublisher = srsPublisher;
  }

  public void start(String endPoint){
    srsPublisher.setOutputResolution(1280, 720);
    srsPublisher.setVideoHDMode();
    srsPublisher.startPublish(endPoint);
  }

  public void stop(){
    srsPublisher.stopPublish();
    srsPublisher.stopRecord();
  }

  public void resume(){
    srsPublisher.resumeRecord();
  }

  public void pause(){
    srsPublisher.pauseRecord();
  }

  public void destroy(){
    stop();
  }

  @Override
  public void onRtmpConnecting(String msg) {

  }

  @Override
  public void onRtmpConnected(String msg) {

  }

  @Override
  public void onRtmpVideoStreaming() {

  }

  @Override
  public void onRtmpAudioStreaming() {

  }

  @Override
  public void onRtmpStopped() {

  }

  @Override
  public void onRtmpDisconnected() {

  }

  @Override
  public void onRtmpVideoFpsChanged(double fps) {

  }

  @Override
  public void onRtmpVideoBitrateChanged(double bitrate) {

  }

  @Override
  public void onRtmpAudioBitrateChanged(double bitrate) {

  }

  @Override
  public void onRtmpSocketException(SocketException e) {

  }

  @Override
  public void onRtmpIOException(IOException e) {

  }

  @Override
  public void onRtmpIllegalArgumentException(IllegalArgumentException e) {

  }

  @Override
  public void onRtmpIllegalStateException(IllegalStateException e) {

  }

  @Override
  public void onNetworkWeak() {

  }

  @Override
  public void onNetworkResume() {

  }

  @Override
  public void onEncodeIllegalArgumentException(IllegalArgumentException e) {

  }

  @Override
  public void onRecordPause() {

  }

  @Override
  public void onRecordResume() {

  }

  @Override
  public void onRecordStarted(String msg) {

  }

  @Override
  public void onRecordFinished(String msg) {

  }

  @Override
  public void onRecordIllegalArgumentException(IllegalArgumentException e) {

  }

  @Override
  public void onRecordIOException(IOException e) {

  }
}
