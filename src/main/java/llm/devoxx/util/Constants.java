package llm.devoxx.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants {
    public static String BLANK_SPACE_REGEX = "[\\t\\n\\r\\u00a0]";
    public static String INTER_WORDS_SPACE_REGEX = "\\s{2,}";
    public static String EMPTY_STRING = "";
    public static String SPACE_ = " ";
    public static final ObjectMapper om = new ObjectMapper();
    public static final String FULL_PATH = "fullPath";
    public static final String DOMAIN_NAME = "domainName";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String ATTACHMENT_TEXT = "attachment_text";
    public static final String DASH = "-";
    public static final String ASTERISK = "*";
    public static final String INDEX_PREFIX = "all-engine-documents";
}
