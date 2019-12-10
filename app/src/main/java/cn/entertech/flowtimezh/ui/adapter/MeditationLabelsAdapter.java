package cn.entertech.flowtimezh.ui.adapter;

import android.view.View;

import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import cn.entertech.flowtimezh.R;
import cn.entertech.flowtimezh.database.ExperimentDimDao;
import cn.entertech.flowtimezh.database.model.ExperimentDimModel;
import cn.entertech.flowtimezh.database.model.ExperimentTagModel;

/**
 * by Entertech
 */
public class MeditationLabelsAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;
    private OnDimClickListener mOnDimClickListener;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MeditationLabelsAdapter(List<MultiItemEntity> data, OnDimClickListener listener) {
        super(data);
        this.mOnDimClickListener = listener;
        addItemType(TYPE_LEVEL_0, R.layout.item_meditation_label_list_parent);
        addItemType(TYPE_LEVEL_1, R.layout.item_meditation_label_list_child);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final MultiItemEntity item) {
        switch (holder.getItemViewType()) {
            case TYPE_LEVEL_0:
                final ExperimentTagModel lv0 = (ExperimentTagModel) item;
                holder.setText(R.id.tv_meditation_label, lv0.getNameCn());
                break;
            case TYPE_LEVEL_1:
                final ExperimentDimModel lv1 = (ExperimentDimModel) item;
                holder.setText(R.id.tv_meditation_dim, lv1.getNameCn());
                if (lv1.isSelected()) {
                    holder.setBackgroundRes(R.id.tv_meditation_dim, R.drawable.shape_meditation_dim_selected_bg);
                    holder.setTextColor(R.id.tv_meditation_dim, ContextCompat.getColor(mContext, R.color.white));
                } else {
                    holder.setBackgroundRes(R.id.tv_meditation_dim, R.drawable.shape_meditation_dim_unselected_bg);
                    holder.setTextColor(R.id.tv_meditation_dim, ContextCompat.getColor(mContext, R.color.colorBlack));
                }

                holder.addOnClickListener(R.id.tv_meditation_dim);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExperimentDimDao experimentDimDao = new ExperimentDimDao(mContext);
                        int tagId = lv1.getTagId();
                        for (int i = 0; i < mData.size(); i++) {
                            if (mData.get(i) instanceof ExperimentTagModel) {
                                ExperimentTagModel tag = (ExperimentTagModel) mData.get(i);
                                if (tag.getId() == tagId) {
                                    List<ExperimentDimModel> dims = tag.getSubItems();
                                    for (int j = 0; j < dims.size(); j++) {
                                        ExperimentDimModel experimentDimModel = dims.get(j);
                                        experimentDimModel.setSelected(false);
                                        experimentDimDao.create(experimentDimModel);
                                    }
                                }
                            }
                        }
                        lv1.setSelected(true);
                        experimentDimDao.create(lv1);
                        mOnDimClickListener.onDimClick();
                        notifyDataSetChanged();
                    }
                });
                break;
            default:
                break;
        }
    }

    public interface OnDimClickListener {
        void onDimClick();
    }
}
