package cn.entertech.flowtimezh.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

import cn.entertech.flowtimezh.R;
import cn.entertech.flowtimezh.database.model.ExperimentDimModel;
import cn.entertech.flowtimezh.database.model.ExperimentTagModel;
import cn.entertech.flowtimezh.entity.LabelsEntity;

/**
 * by Entertech
 */
public class LabelsAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public LabelsAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.item_label_list_parent);
        addItemType(TYPE_LEVEL_1, R.layout.item_label_list_child);
    }

    @Override
    protected void convert(final BaseViewHolder holder, final MultiItemEntity item) {
        switch (holder.getItemViewType()) {
            case TYPE_LEVEL_0:
                final ExperimentTagModel lv0 = (ExperimentTagModel) item;
                holder.setText(R.id.tv_label_name, lv0.getNameCn());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        if (lv0.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    }
                });
                break;
            case TYPE_LEVEL_1:
                final ExperimentDimModel lv1 = (ExperimentDimModel) item;
                holder.setText(R.id.tv_dim_name, lv1.getNameCn());
                break;
            default:
                break;
        }
    }
}
