package events.dismember;

import entity.dismember.ApkVersion;

public class ApkSourceUploadEvent {
	private ApkVersion apkVersion;

	public ApkSourceUploadEvent() {
		super();
	}
	public ApkSourceUploadEvent(ApkVersion apkVersion) {
		super();
		this.apkVersion = apkVersion;
	}
	public ApkVersion getApkVersion() {
		return apkVersion;
	}
	public void setApkVersion(ApkVersion apkVersion) {
		this.apkVersion = apkVersion;
	}
}
