package cn.entertech.flowtimezh.mvp.model.meditation

class ReportMeditationDataEntity {
    @Volatile
    var reportPleasureEnitty: ReportPleasureEnitty? = null
    @Volatile
    var reportAttentionEnitty: ReportAttentionEnitty? = null
    @Volatile
    var reportPressureEnitty: ReportPressureEnitty? = null
    @Volatile
    var reportRelaxationEnitty: ReportRelaxationEnitty? = null
    @Volatile
    var reportHRDataEntity: ReportHRDataEntity? = null
    @Volatile
    var reportEEGDataEntity: ReportEEGDataEntity? = null
    @Volatile
    var reportCoherenceEnitty: ReportCoherenceEnitty? = null

    fun isDataSetCompletly(): Boolean {
        synchronized(ReportMeditationDataEntity::class.java) {
            return (reportEEGDataEntity != null && reportHRDataEntity != null && reportRelaxationEnitty != null && reportPressureEnitty != null && reportAttentionEnitty != null && reportPleasureEnitty != null && reportCoherenceEnitty != null)
        }
    }

    override fun toString(): String {
        return "ReportMeditationDataEntity(reportPleasureEnitty=$reportPleasureEnitty, reportAttentionEnitty=$reportAttentionEnitty, reportPressureEnitty=$reportPressureEnitty, reportRelaxationEnitty=$reportRelaxationEnitty, reportHRDataEntity=$reportHRDataEntity, reportEEGDataEntity=$reportEEGDataEntity, reportCoherenceEnitty=$reportCoherenceEnitty)"
    }

}