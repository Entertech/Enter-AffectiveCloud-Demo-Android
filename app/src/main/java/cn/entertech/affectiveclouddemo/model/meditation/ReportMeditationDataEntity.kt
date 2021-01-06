package cn.entertech.affectiveclouddemo.model.meditation;

class ReportMeditationDataEntity {
    @Volatile var  reportPleasureEntity: ReportPleasureEntity? = null
    @Volatile var  reportCoherenceEntity: ReportCoherenceEntity? = null
    @Volatile var reportAttentionEntity: ReportAttentionEntity? = null
    @Volatile var reportPressureEntity: ReportPressureEntity? = null
    @Volatile var reportRelaxationEntity: ReportRelaxationEntity? = null
    @Volatile var reportHRDataEntity: ReportHRDataEntity? = null
    @Volatile var reportEEGDataEntity: ReportEEGDataEntity? = null


    fun isDataSetCompletely(): Boolean {
        synchronized(ReportMeditationDataEntity::class.java) {
            return (reportEEGDataEntity != null && reportHRDataEntity != null && reportRelaxationEntity != null && reportPressureEntity != null && reportAttentionEntity != null && reportPleasureEntity != null && reportCoherenceEntity != null)
        }
    }

    override fun toString(): String {
        return "ReportMeditationDataEntity(reportPleasureEnitty=$reportPleasureEntity, reportCoherenceEnitty=$reportCoherenceEntity, reportAttentionEnitty=$reportAttentionEntity, reportPressureEnitty=$reportPressureEntity, reportRelaxationEnitty=$reportRelaxationEntity, reportHRDataEntity=$reportHRDataEntity, reportEEGDataEntity=$reportEEGDataEntity)"
    }

}