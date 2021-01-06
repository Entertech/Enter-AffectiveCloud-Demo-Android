package cn.entertech.affectiveclouddemo.app


class Constant {
    companion object {

        const val BASE_URL = "https://api.myflowtime.com/"
        const val CLIENT_ID = "UikxI5w7VjipXcHMgUJDbX9d6lFfhKHjg0NpKsXU"
        //        const val CLIENT_ID = "r5OtYyGav0DmmsYeX2xg7wCn2UqDcPIfNIENHRML"
        const val SP_SETTING = "setting"
        const val TOKEN = "token"
        const val USER_ID = "userId"
        const val SOCIAL_USER_ID = "socialUserId"
        const val SOCIAL_USER_NAME = "socialUserName"
        const val SOCIAL_IMAGE = "socialImage"
        const val SOCIAL_TYPE = "socialType"
        const val REFRESH_TOKEN = "refreshToken"
        const val CAN_ACCESS_PREMIUM = "canAccessPremium"
        const val IS_CONNECT_BEFORE = "isConnectBefore"
        const val BLE_HARDWARE = "ble_hardware"
        const val BLE_FIRMWARE = "ble_fireware"
        const val BLE_MAC = "ble_mac"
        const val IS_TIP_UP_NEXT = "isTipUpNext"
        const val IS_TIP_FLOWTIME = "isTipFlowtime"
        const val IS_TIP_MY_COURSE = "isTipMyCourse"
        const val IS_TIP_TOP_COURSE = "isTipTopCourse"
        const val IS_TIP_JOURNEY = "isTipJourney"
        const val IS_TIP_UNGUIDE_MEDITATION = "isTipUnguideMeditation"
        const val MEDITATION_VIEW_ORDER = "meditationViewOrder"
        const val STATISTICS_VIEW_ORDER = "statisticsViewOrder"
        const val IS_FIRST_LOAD_VIEW = "isFirstLoadView"
        const val REMOTE_CONFIG_HELP_CENTER = "help_center"
        const val REMOTE_CONFIG_PRESSURE_REPORT_INFO = "pressure_report_info"
        const val REMOTE_CONFIG_RELAXATION_REPORT_INFO = "relaxation_report_info"
        const val REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO = "attention_and_relaxation_report_info"
        const val REMOTE_CONFIG_ATTENTION_REPORT_INFO = "attention_report_info"
        const val REMOTE_CONFIG_HRV_REPORT_INFO = "hrv_report_info"
        const val REMOTE_CONFIG_HRV_REALTIME_INFO = "hrv_realtime_info"
        const val REMOTE_CONFIG_HR_REPORT_INFO = "hr_report_info"
        const val REMOTE_CONFIG_BRAIN_REPORT_INFO = "brainwave_spectrum_report_info"
        const val REMOTE_CONFIG_PRESSURE_REALTIME_INFO = "pressure_realtime_info"
        const val REMOTE_CONFIG_RELAXATION_REALTIME_INFO = "relaxation_realtime_info"
        const val REMOTE_CONFIG_ATTENTION_REALTIME_INFO = "attention_realtime_info"
        const val REMOTE_CONFIG_BRAIN_REALTIME_INFO = "brainwave_spectrum_realtime_info"
        const val REMOTE_CONFIG_EEG_REALTIME_INFO = "eeg_realtime_info"
        const val REMOTE_CONFIG_HR_REALTIME_INFO = "hr_realtime_info"
        const val REMOTE_CONFIG_FLOWTIME_HEADHAND_INTRO = "flowtime_headband_intro"
        const val REMOTE_CONFIG_TERMS_OF_USER = "terms_of_use"
        const val REMOTE_CONFIG_PRIVACY = "privacy"
        const val REMOTE_CONFIG_COHERENCE_REALTIME_INFO = "coherence_realtime_info"
        const val REMOTE_CONFIG_PRODUCT_PRICE = "product_price"
        const val REMOTE_CONFIG_FILL_ADDRESS = "fill_address"
        const val REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT = "device_can_not_connect"
        const val LOCAL_VERSION_NAME = "local_version_name"
        const val APP_COULD_UPDATE = "app_could_update"
        const val REMOTE_CONFIG_FIRMWARE_VERSION = "new_firmware_version"
        const val REMOTE_CONFIG_APP_VERSION = "new_app_version"
        const val REMOTE_CONFIG_FIRMWARE_URL = "firmware_url"

        const val DEFAULT_LINK_FILL_ADDRESS = "https://jinshuju.net/f/tQP3iq"
        const val DEFAULT_LINK_HELP_CENTER =
            "https://docs.myflowtime.cn/"
        const val DEFAULT_LINK_PRESSURE_REPORT_INFO =
            "https://docs.myflowtime.cn/%E7%9C%8B%E6%87%82%E5%9B%BE%E8%A1%A8/%E5%A6%82%E4%BD%95%E7%9C%8B%E5%8E%8B%E5%8A%9B%E6%B0%B4%E5%B9%B3%E6%9B%B2%E7%BA%BF%EF%BC%9F.html"
        const val DEFAULT_LINK_ATTENTION_RELAXATION_REPORT_INFO =
            "https://docs.myflowtime.cn/%E7%9C%8B%E6%87%82%E5%9B%BE%E8%A1%A8/%E5%A6%82%E4%BD%95%E7%9C%8B%E6%B3%A8%E6%84%8F%E5%8A%9B%E5%92%8C%E6%94%BE%E6%9D%BE%E5%BA%A6%E6%9B%B2%E7%BA%BF%EF%BC%9F.html"
        const val DEFAULT_LINK_ATTENTION_REPORT_INFO =
            "https://www.notion.so/Attention-Graph-8f9fa5017ba74a34866c1977a323960a"
        const val DEFAULT_LINK_HRV_REPORT_INFO = "https://docs.myflowtime.cn/%E7%9C%8B%E6%87%82%E5%9B%BE%E8%A1%A8/%E5%A6%82%E4%BD%95%E7%9C%8B%E5%BF%83%E7%8E%87%E5%8F%98%E5%BC%82%E6%80%A7%EF%BC%88HRV%EF%BC%89%E7%9A%84%E5%8F%98%E5%8C%96%E6%9B%B2%E7%BA%BF%EF%BC%9F.html"
        const val DEFAULT_LINK_HRV_REALTIME_INFO = "https://docs.myflowtime.cn/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A/%E5%BF%83%E7%8E%87%E5%8F%98%E5%BC%82%E6%80%A7%EF%BC%88HRV%EF%BC%89.html"
        const val DEFAULT_LINK_HR_REPORT_INFO =
            "https://docs.myflowtime.cn/%E7%9C%8B%E6%87%82%E5%9B%BE%E8%A1%A8/%E5%A6%82%E4%BD%95%E7%9C%8B%E5%BF%83%E7%8E%87%E5%8F%98%E5%8C%96%E6%9B%B2%E7%BA%BF%EF%BC%9F.html"
        const val DEFAULT_LINK_BRAIN_REPORT_INFO =
            "https://docs.myflowtime.cn/%E7%9C%8B%E6%87%82%E5%9B%BE%E8%A1%A8/%E5%A6%82%E4%BD%95%E7%9C%8B%E8%84%91%E6%B3%A2%E9%A2%91%E8%B0%B1%E8%83%BD%E9%87%8F%E8%B6%8B%E5%8A%BF%E5%9B%BE%EF%BC%9F.html"
        const val DEFAULT_LINK_PRESSURE_REALTIME_INFO =
            "https://docs.myflowtime.cn/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A/%E5%8E%8B%E5%8A%9B%E6%B0%B4%E5%B9%B3.html"
        const val DEFAULT_LINK_RELAXATION_REALTIME_INFO =
            "https://docs.myflowtime.cn/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A/%E6%94%BE%E6%9D%BE%E5%BA%A6.html"
        const val DEFAULT_LINK_ATTENTION_REALTIME_INFO =
            "https://docs.myflowtime.cn/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A/%E6%B3%A8%E6%84%8F%E5%8A%9B.html"
        const val DEFAULT_LINK_BRAIN_REALTIME_INFO =
            "https://docs.myflowtime.cn/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A/%E8%84%91%E7%94%B5%E6%B3%A2%E8%8A%82%E5%BE%8B%EF%BC%88Brainwave%20Rhythms%EF%BC%89.html"
        const val DEFAULT_LINK_DEVICE_CAN_NOT_CONNECT =
            "https://docs.myflowtime.cn/%E8%AE%BE%E5%A4%87%E4%BD%BF%E7%94%A8/%E5%BF%83%E6%B5%81%E5%A4%B4%E7%8E%AF%E4%BD%BF%E7%94%A8%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9.html"
        const val DEFAULT_LINK_EEG_REALTIME_INFO = "https://docs.myflowtime.cn/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A/%E8%84%91%E7%94%B5%E6%B3%A2%EF%BC%88EEG%EF%BC%89.html"
        const val DEFAULT_LINK_HR_REALTIME_INFO = "https://docs.myflowtime.cn/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A/%E5%BF%83%E7%8E%87.html"
        const val DEFAULT_LINK_FLOWTIME_HEADHAND_INTRO = "https://docs.myflowtime.cn/%E8%AE%BE%E5%A4%87%E4%BD%BF%E7%94%A8/%E5%BF%83%E6%B5%81%E5%A4%B4%E7%8E%AF%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.html"
        const val DEFAULT_LINK_TERMS_OF_USER = "https://www.entertech.cn/term-of-use"
        const val DEFAULT_LINK_PRIVACY = "https://www.entertech.cn/privacy"
        const val DEFAULT_LINK_COHERENCE_REALTIME =
            "https://docs.myflowtime.cn/名词解释/和谐度（Coherence）.html"

        const val ITEM_PURCHASE = "itemPurchase"
        const val MEDITATION_UNGUIDED_TIME = "meditationUnguideTime"
        const val MEDITATION_TIME_IS_CUSTOM = "meditationTimeIsCustom"
        const val MEDITATION_TIME_SELECTED_INDEX = "meditationTimeSelectedIndex"
        const val MEDITATION_TIME_IS_RESET = "meditationTimeIsReset"

        const val MEDITATIONE_EDN_SOUND_INDEX = "meditationEndSoundIndex"
        const val MEDITATIONE_EDN_SOUND_RES = "meditationEndSoundRes"
        var GOOGLE_BILLING_PUBLIC_KEY =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj90cVrtPS/xN2ZunV/tgG/DUfiIzbmtrsq/grALbNfytYEeA9EMYHeHgbrKQkXUrku2bkBluRJPsQhjZXxPqRIfV9V95HEfcTu6sAuAg/4cCGYwpHSwxCzO9atbPhprycx378OX4YEOjannniMiByk/7VOA47MFBxJyd9P/a/Dwgj1YMZWU+43y3hld/dmHugoD202AeUmc7Z2pCvHYkQthW8vBIIWCtm7Xk97kmKLCXutb8kfQ0n/vyxyDECfCRb5LACN1YQ7Zb+ngA23wZD3LGlSKYMHbftA0YwU9wg4Q3gjJEoqIEoNRM3cJq54pmuJYrknZdnI/A+MktlXcb2QIDAQAB"

        //        intent
        var COLLECTION_NAME = "collectionName"
        var COLLECTION_ID = "collectionId"
        var COLLECTION_DESCRIPTION = "collectionDescription"
        var COLLECTION_COLOR = "collectionColor"
        var COLLECTION_COURSES = "collectionCourses"
        var COURSE_NAME = "courseName"
        var COURSE_DESCRIPTION = "courseDescription"
        var COURSE_IMAGE = "courseImage"
        var COURSE_AUTHOR_ID = "courseAuthorId"
        var COURSE_ID = "courseId"
        var LESSON_ID = "lessonId"
        var LESSON_FILE_URL = "lessonFileUrl"
        var LESSON_NAME = "lessonName"
        var LESSON_INDEX = "lessonIndex"
        var RECORD_ID = "recordId"
        var AUTHOR_IMAGE = "authorImage"
        var AUTHOR_NAME = "authorName"
        var AUTHOR_DESCRIPTION = "authorDescription"
        var APP_UPDATE_VERSION_LEVEL = "versionLevel"
        var APP_UPDATE_VERSION_NOTES = "versionNotes"

        //        billing product id
        val PRODUCT_ID_SUB_MONTH = "android_1month_subscription"
        val PRODUCT_ID_SUB_YEAR = "android_12months_subscription"
        val PRODUCT_ID_ITEM_LIFETIME = "android_flowtime_lifetime"
        val PRODUCT_ID_ITEM_LIFETIME_PLUS = "android_flowtime_lifetime_plus"

        const val DEFAULT_STATISTICS_VIEW_ORDER =
            "[{\"isShow\":true,\"name\":\"Brainwave Sepctrum\"},{\"isShow\":true,\"name\":\"Heart Rate\"},{\"isShow\":true,\"name\":\"Heart Rate Variability\"},{\"isShow\":true,\"name\":\"Attention\"},{\"isShow\":true,\"name\":\"Relaxation\"},{\"isShow\":true,\"name\":\"Pressure\"}]"

        const val FEEDBACK_EMAIL_ADDRESS = "customer@entertech.cn"
        const val FEEDBACK_EMAIL_SUBJECT = "Flowtime - App Feedback"
        const val BUGSTAG_APP_KEY = "ae327eebd1726879105bac19dfce061a"
        const val WX_LOGIN_APP_ID = "wxc49d0fcd20950041"
        const val WX_LOGIN_APP_SECRET = "a649b7525fcbc26886757daa21b11b52"
        const val TWITTER_LOGIN_CONSUMER_KEY = "DzGeSiizRuVgHvZKEuxZtUiJF"
        const val TWITTER_LOGIN_CONSUMER_SECRET = "GAzkPeBZweP5PK3FlSJ0dzzxFshvHsXpi1lanqVe1ShEUMuvTK"
        const val GOOGLE_LOGIN_CLIENT_ID = "370288935156-bcm09d3nr3ddljejc7tvgll8pt1e3b4q.apps.googleusercontent.com"

        var MEDITATION_TYPE = "meditationType"
        var IS_SHOW_SKIP = "isShowSkip"
        const val SP_DEFAULT_FIRMWARE_URL = "http://heartflow.oss-cn-hangzhou.aliyuncs.com/firmware/"
        const val SP_SERVER_FIRMWARE_VERSION = "serverFirmwareVersion"
        const val SP_SERVER_FIRMWARE_URL = "serverFirmwareUrl"
        const val SP_SERVER_APP_VERSION = "serverAppVersion"
        const val SP_LOG_TOKEN = "logToken"
        const val SP_BLE_MAC = "bleMac"
        const val SP_BRAIN_CHART_LEGEND_SHOW_LIST = "brainLegendList"
    }
}