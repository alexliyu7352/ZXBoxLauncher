package com.zx.zxboxlauncher.page;

import android.view.View;

import com.zx.zxboxlauncher.R;
import com.zx.zxboxlauncher.activity.MainActivity;

/**
 * User: ShaudXiao
 * Date: 2017-03-15
 * Time: 15:48
 * Company: zx
 * Description:
 * FIXME
 */


public class HomePage  extends  BasePage implements View.OnClickListener {

    public HomePage(MainActivity activity) {
        super(activity);
    }

    @Override
    public int getLayoutViewId() {
        return R.layout.layout_paper_view_1;
    }

    @Override
    public void initViews() {
    }

    @Override
    public void onClick(View v) {

    }
}
