package cn.entertech.flowtimezh.mvp.view

import cn.entertech.flowtimezh.mvp.model.ReminderSettingEntity


interface ReminderSettingView : View {
    fun onGetReimderSettingSuccess(reminderSettings: List<ReminderSettingEntity>)
    fun onGetReimderSettingError(error: String)

    fun onPostReimderSettingSuccess(reminderSetting: ReminderSettingEntity)
    fun onPostReimderSettingError(error: String)

    fun onUpdateReimderSettingSuccess(reminderSetting: ReminderSettingEntity)
    fun onUpdateReimderSettingError(error: String)
}