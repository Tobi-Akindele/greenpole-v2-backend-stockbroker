package com.ap.greenpole.stockbroker.utils;

public class ConstantUtils {

	public static final String[] REQUEST_TYPES = { "CREATE_STOCKBROKER_ACCOUNT", "UPDATE_STOCKBROKER_ACCOUNT",
			"DEACTIVATE_STOCKBROKER_ACCOUNT", "STOCKBROKER_ACCOUNT_VALIDATION", "STOCKBROKER_ACCOUNT_SUSPENSION"};
	public static final String[] APPROVAL_STATUS = { "", "Pending", "Rejected", "Accepted" };
	public static final String[] APPROVAL_ACTIONS = { "Accepted", "Rejected" };
	public static final String[] VALIDATION_ACTIONS = { "VALIDATE", "INVALIDATE" };
	public static final String[] VALIDATION_STATUS = { "VALIDATED", "INVALIDATED" };
	public static final String[] SUSPENSION_ACTIONS = { "SUSPEND", "REINSTATE" };
	public static final String[] SUSPENSION_STATUS = { "SUSPENDED", "REINSTATED" };
	public static final String UNDEFINED = "UNDEFINED";
	public static final String MODULE_APPROVAL_PERMISSION = "STOCKBROKER_APPROVAL";
	public static final String[] SUPPORTED_QUERY_EXPORT_FORMATS = { "CSV", "XLSX", "PDF" };
	public static final String QUERY_EXPORT = "QUERY_EXPORT";
	public static final String DIRECTORY = "/Exports/";
	public static final String[] FILE_STORAGE_PATHS = { "TEMP_DIR", "PMT_DIR" };
	public static final String[] FILE_UPLOAD_TYPES = { "image/jpeg", "image/png", "image/gif", "image/bmp",
			"image/jpg" };
	public static final int PENDING = 1;
	public static final int REJECTED = 2;
	public static final int ACCEPTED = 3;
	public static final String AUTHORIZATION = "Authorization";
	public static final String MODULE = "STOCKBROKER";
	public static final String PENDING_URL = "/notification/send/pending_approval";
	public static final String APPROVED_URL = "/notification/send/successful_approval";
	public static final String REJECTED_URL = "/notification/send/rejected_approval";
	public static final String FILTER_DATE_FORMAT = "yyyy-MM-dd";
	public static final String[] TYPES = {"SENT", "RECEIVED"};
}
