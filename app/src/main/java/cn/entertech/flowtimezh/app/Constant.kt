package cn.entertech.flowtimezh.app


class Constant {
    companion object {

        const val BASE_URL = "https://api-test.myflowtime.cn/"
        const val CLIENT_ID = "UikxI5w7VjipXcHMgUJDbX9d6lFfhKHjg0NpKsXU"
        //        const val CLIENT_ID = "r5OtYyGav0DmmsYeX2xg7wCn2UqDcPIfNIENHRML"
        const val SP_SETTING = "setting"
        const val TOKEN = "token"
        const val AFFECTIVE_CLOUD_ADDRESS = "affective_cloud_address"
        const val AFFECTIVE_APP_KEY = "affective_cloud_key"
        const val AFFECTIVE_APP_SECRET = "affective_cloud_secret"
        const val LOG_TOKEN = "logToken"
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
        const val BLE_FIRMWARE_CLOUD = "ble_fireware_cloud"
        const val BLE_FIRMWARE_CLOUD_UPDATE_NOTES = "ble_fireware_cloud_update_notes"
        const val BLE_MAC = "ble_mac"
        const val IS_TIP_UP_NEXT = "isTipUpNext"
        const val IS_TIP_FLOWTIME = "isTipFlowtime"
        const val IS_TIP_MY_COURSE = "isTipMyCourse"
        const val IS_TIP_TOP_COURSE = "isTipTopCourse"
        const val IS_TIP_JOURNEY = "isTipJourney"
        const val IS_TIP_CONTACT = "isTipContact"
        const val IS_TIP_UNGUIDE_MEDITATION = "isTipUnguideMeditation"
        const val MEDITATION_VIEW_ORDER = "meditationViewOrder"
        const val STATISTICS_VIEW_ORDER = "statisticsViewOrder"
        const val IS_FIRST_LOAD_VIEW = "isFirstLoadView"
        const val REMOTE_CONFIG_RATE_CONDITION = "rate_condition"
        const val REMOTE_CONFIG_SHARE_CONDITION= "share_condition"
        const val REMOTE_CONFIG_HELP_CENTER = "help_center"
        const val REMOTE_CONFIG_PRESSURE_REPORT_INFO = "pressure_report_info"
        const val REMOTE_CONFIG_RELAXATION_REPORT_INFO = "relaxation_report_info"
        const val REMOTE_CONFIG_ATTENTION_REPORT_INFO = "attention_report_info"
        const val REMOTE_CONFIG_HRV_REPORT_INFO = "hrv_report_info"
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
        const val REMOTE_CONFIG_PRODUCT_PRICE = "product_price"
        const val REMOTE_CONFIG_FILL_ADDRESS = "fill_address"
        const val REMOTE_CONFIG_DEVICE_CAN_NOT_CONNECT = "device_can_not_connect"
        const val REMOTE_CONFIG_HRV_REALTIME_INFO = "hrv_realtime_info"
        const val REMOTE_CONFIG_ATTENTION_RELAXATION_REPORT_INFO = "attention_and_relaxation_report_info"
        const val REMOTE_CONFIG_COHERENCE_REALTIME_INFO = "coherence_realtime_info"
        const val LOCAL_VERSION_NAME = "local_version_name"
        const val APP_COULD_UPDATE = "app_could_update"
        const val APP_RATE = "app_rate"
        const val IS_CUSTOM_MEDITATION_TIME = "is_custom_meditation_time"
        const val MEDITATION_TIME = "meditation_time"
        const val CUSTOM_MEDITATION_TIME = "custom_meditation_time"

        const val DEFAULT_RATE_CONDITION = "{\"min_meditation_time\":8,\"min_data_valid_ratio\":0.8,\"min_r&a_value_1\":75,\"min_r&a_value_2\":60}"
        const val DEFAULT_SHARE_CONDITION = "{\"report_page_share_conditions\":{\"min_meditation_time\":8,\"min_hr\":50,\"min_r&a_value\":70,\"max_pressure_value\":40},\"course_page_share_conditions\":{\"min_meditation_time\":8,\"min_meditation_total_time\":30},\"hrv_page_share_conditions\":{\"min_data_valid_ratio\":0.8},\"pressure_page_share_conditions\":{\"min_data_valid_ratio\":0.8,\"max_pressure_value\":40},\"r&a_page_share_conditions\":{\"min_data_valid_ratio\":0.8,\"min_r&a_value\":70}}"
        const val DEFAULT_LINK_FILL_ADDRESS = "https://jinshuju.net/f/tQP3iq"
        const val DEFAULT_LINK_HELP_CENTER =
            "https://www.notion.so/Flowtime-Help-Center-b151d8677e5c41d8af6364f44fb93369"
        const val DEFAULT_LINK_PRESSURE_REPORT_INFO =
            "https://www.notion.so/Pressure-Graph-48593014d6e44f7f8366364d70dced05"
        const val DEFAULT_LINK_RELAXATION_REPORT_INFO =
            "https://www.notion.so/Relaxation-Graph-d04c7d161ca94c6eb9c526cdefe88f02"
        const val DEFAULT_LINK_ATTENTION_REPORT_INFO =
            "https://www.notion.so/Attention-Graph-8f9fa5017ba74a34866c1977a323960a"
        const val DEFAULT_LINK_HRV_REPORT_INFO = "https://www.notion.so/HRV-Graph-6f93225bf7934cb8a16eb6ba55da52cb"
        const val DEFAULT_LINK_HR_REPORT_INFO =
            "https://www.notion.so/Heart-Rate-Graph-fa83da8528694fd1a265882db31d3778"
        const val DEFAULT_LINK_BRAIN_REPORT_INFO =
            "https://www.notion.so/Brainwave-Power-Graph-6f2a784b347d4d7d98b9fd0da89de454"
        const val DEFAULT_LINK_PRESSURE_REALTIME_INFO =
            "https://www.notion.so/Pressure-ee57f4590373442b9107b7ce665e1253"
        const val DEFAULT_LINK_RELAXATION_REALTIME_INFO =
            "https://www.notion.so/Relaxation-c9e3b39634a14d2fa47eaed1d55d872b"
        const val DEFAULT_LINK_ATTENTION_REALTIME_INFO =
            "https://www.notion.so/Attention-84fef81572a848efbf87075ab67f4cfe"
        const val DEFAULT_LINK_BRAIN_REALTIME_INFO =
            "https://www.notion.so/Brainwave-Power-4cdadda14a69424790c2d7913ad775ff"
        const val DEFAULT_LINK_DEVICE_CAN_NOT_CONNECT =
            "https://www.notion.so/I-can-t-connect-the-headband-with-the-app-1ae10dc7fe1049c4953fc879f9042730"
        const val DEFAULT_LINK_EEG_REALTIME_INFO = "https://www.notion.so/EEG-b3a44e9eb01549c29da1d8b2cc7bc08d"
        const val DEFAULT_LINK_HR_REALTIME_INFO = "https://www.notion.so/Heart-Rate-4d64215ac50f4520af7ff516c0f0e00b"
        const val DEFAULT_LINK_FLOWTIME_HEADHAND_INTRO = "https://www.meetinnerpeace.com/flowtime"
        const val DEFAULT_LINK_TERMS_OF_USER = "https://www.meetinnerpeace.com/terms-of-service"
        const val DEFAULT_LINK_PRIVACY = "https://www.meetinnerpeace.com/privacy-policy"
        const val DEFAULT_LINK_HRV_REALTIME_INFO = "https://docs.myflowtime.cn/%E5%90%8D%E8%AF%8D%E8%A7%A3%E9%87%8A/%E5%BF%83%E7%8E%87%E5%8F%98%E5%BC%82%E6%80%A7%EF%BC%88HRV%EF%BC%89.html"
        const val DEFAULT_LINK_ATTENTION_RELAXATION_REPORT_INFO =
            "https://docs.myflowtime.cn/%E7%9C%8B%E6%87%82%E5%9B%BE%E8%A1%A8/%E5%A6%82%E4%BD%95%E7%9C%8B%E6%B3%A8%E6%84%8F%E5%8A%9B%E5%92%8C%E6%94%BE%E6%9D%BE%E5%BA%A6%E6%9B%B2%E7%BA%BF%EF%BC%9F.html"
        const val DEFAULT_LINK_COHERENCE_REALTIME =
            "https://docs.myflowtime.cn/名词解释/和谐度（Coherence）.html"

        const val ITEM_PURCHASE = "itemPurchase"
        const val MEDITATION_UNGUIDED_TIME = "meditationUnguideTime"
        const val MEDITATION_TIME_IS_CUSTOM = "meditationTimeIsCustom"
        const val MEDITATION_TIME_SELECTED_INDEX = "meditationTimeSelectedIndex"
        const val MEDITATION_TIME_IS_RESET = "meditationTimeIsReset"

        const val REMOTE_CONFIG_FIRMWARE_VERSION = "new_firmware_version"
        const val REMOTE_CONFIG_APP_VERSION = "new_app_version"
        const val REMOTE_CONFIG_FIRMWARE_URL = "firmware_url"
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
        var FIRMWARE_PATH = "firmwarePath"
        var UPDATE_NOTES = "updateNotes"
        var IS_FROM_MEDITATION = "isFromMeditation"
        var MEDITATION_TYPE = "meditationType"
        var IS_SHOW_SKIP = "isShowSkip"
        var MEDITATION_TYPE_GUIDE = "guide"
        var MEDITATION_TYPE_UNGUIDE = "unguide"

        //        billing product id
        val PRODUCT_ID_SUB_MONTH = "android_1month_subscription"
        val PRODUCT_ID_SUB_YEAR = "android_12months_subscription"
        val PRODUCT_ID_ITEM_LIFETIME = "android_flowtime_lifetime"
        val PRODUCT_ID_ITEM_LIFETIME_PLUS = "android_flowtime_lifetime_plus"

        const val DEFAULT_STATISTICS_VIEW_ORDER =
            "[{\"isShow\":true,\"name\":\"Brainwave Sepctrum\"},{\"isShow\":true,\"name\":\"Heart Rate\"},{\"isShow\":true,\"name\":\"Heart Rate Variability\"},{\"isShow\":true,\"name\":\"Attention\"},{\"isShow\":true,\"name\":\"Relaxation\"},{\"isShow\":true,\"name\":\"Pressure\"}]"

        const val FEEDBACK_EMAIL_ADDRESS = "customer@entertech.cn"
        const val FEEDBACK_EMAIL_SUBJECT = "心流 - App Feedback"
        const val BUGSTAG_APP_KEY = "ae327eebd1726879105bac19dfce061a"
        const val WX_LOGIN_APP_ID = "wxa8a5c684e0425f48"
        const val WX_LOGIN_APP_SECRET = "69235dff281112a29967a8d9df4db22a"
        const val TWITTER_LOGIN_CONSUMER_KEY = "DzGeSiizRuVgHvZKEuxZtUiJF"
        const val TWITTER_LOGIN_CONSUMER_SECRET = "GAzkPeBZweP5PK3FlSJ0dzzxFshvHsXpi1lanqVe1ShEUMuvTK"
        const val GOOGLE_LOGIN_CLIENT_ID = "370288935156-bcm09d3nr3ddljejc7tvgll8pt1e3b4q.apps.googleusercontent.com"


        //        social login type
        const val SOCIAL_LOGIN_TYPE_FACEBOOK = "facebook"
        const val SOCIAL_LOGIN_TYPE_GOOGLE = "google"
        const val SOCIAL_LOGIN_TYPE_TWITTER = "twitter"
        const val SOCIAL_LOGIN_TYPE_WECHAT = "wechat"

        const val APP_UPDATE_LEVEL_MUST = 0
        const val APP_UPDATE_LEVEL_SHOULD = 1
        const val APP_UPDATE_LEVEL_COULD = 2

        const val REPORT_SHARE_PAGE_REPORT = 0
        const val REPORT_SHARE_PAGE_COURSE = 1
        const val REPORT_SHARE_PAGE_HRV = 2
        const val REPORT_SHARE_PAGE_PRESSURE = 3
        const val REPORT_SHARE_PAGE_R_A = 4

        const val SP_DEFAULT_FIRMWARE_URL = "http://heartflow.oss-cn-hangzhou.aliyuncs.com/firmware/"
        const val SP_SERVER_FIRMWARE_VERSION = "serverFirmwareVersion"
        const val SP_SERVER_FIRMWARE_URL = "serverFirmwareUrl"
        const val SP_SERVER_APP_VERSION = "serverAppVersion"

        const val UNGUIDE_COURSE_ID = 1
        const val UNGUIDE_LESSON_ID = 1

        const val WEB_TITLE = "webTitle"
        const val EXTRA_URL = "url"

    }
}