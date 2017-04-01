package com.byox.drawviewproject.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;

import com.byox.drawviewproject.R;
import com.ch4vi.flowlayoutmanager.FlowLayoutManager;

import org.jetbrains.annotations.NotNull;

import kotlin.Pair;

/**
 * Created by IngMedina on 28/03/2017.
 */

public class LayoutUtils {
    private static final int PHONE_PORTRAIT_COLUMNS_LAYOUT_COLUMNS = 3;
    private static final int PHONE_LANDSCAPE_COLUMNS_LAYOUT_COLUMNS = 5;

    private static final Object[] PORTRAIT_FLOW_LAYOUT_PATTERN =
            new Object[]{new Pair<>(3, 2), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(2, 2),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(3, 3), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 2), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(2, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 2),
                    new Pair<>(2, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1)};
    private static final Object[] LANDSCAPE_FLOW_LAYOUT_PATTERN =
            new Object[]{new Pair<>(3, 3), new Pair<>(2, 2), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(2, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(2, 2), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(3, 3), new Pair<>(1, 1), new Pair<>(1, 2),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(2, 2),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(2, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1),
                    new Pair<>(1, 1), new Pair<>(1, 1), new Pair<>(1, 1)};
    private static int mFlowLayoutPositionMultiplier = 0;


    public static RecyclerView.LayoutManager GetFlowLayoutManager(Context context) {
        mFlowLayoutPositionMultiplier = 0;
        RecyclerView.LayoutManager layoutManager = null;
        if (!context.getResources().getBoolean(R.bool.isTablet)) {
            switch (context.getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    layoutManager = new FlowLayoutManager(PHONE_PORTRAIT_COLUMNS_LAYOUT_COLUMNS,
                            RecyclerView.VERTICAL, new FlowLayoutManager.Interface() {
                        @NotNull
                        @Override
                        public Pair<Integer, Integer> getProportionalSizeForChild(int i) {
                            if (i == 0)
                                mFlowLayoutPositionMultiplier = 0;

                            Pair<Integer, Integer> pair;

                            if (i - (PORTRAIT_FLOW_LAYOUT_PATTERN.length * mFlowLayoutPositionMultiplier) ==
                                    PORTRAIT_FLOW_LAYOUT_PATTERN.length)
                                mFlowLayoutPositionMultiplier++;

                            pair = (Pair<Integer, Integer>)
                                    PORTRAIT_FLOW_LAYOUT_PATTERN[i - (PORTRAIT_FLOW_LAYOUT_PATTERN.length * mFlowLayoutPositionMultiplier)];

                            return pair;
                        }
                    });
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    layoutManager = new FlowLayoutManager(PHONE_LANDSCAPE_COLUMNS_LAYOUT_COLUMNS,
                            RecyclerView.VERTICAL, new FlowLayoutManager.Interface() {
                        @NotNull
                        @Override
                        public Pair<Integer, Integer> getProportionalSizeForChild(int i) {
                            if (i == 0)
                                mFlowLayoutPositionMultiplier = 0;

                            Pair<Integer, Integer> pair;

                            if (i - (LANDSCAPE_FLOW_LAYOUT_PATTERN.length * mFlowLayoutPositionMultiplier) ==
                                    LANDSCAPE_FLOW_LAYOUT_PATTERN.length)
                                mFlowLayoutPositionMultiplier++;

                            pair = (Pair<Integer, Integer>)
                                    LANDSCAPE_FLOW_LAYOUT_PATTERN[i - (LANDSCAPE_FLOW_LAYOUT_PATTERN.length * mFlowLayoutPositionMultiplier)];

                            return pair;
                        }
                    });
                    break;
            }
        }

        return layoutManager;
    }
}
