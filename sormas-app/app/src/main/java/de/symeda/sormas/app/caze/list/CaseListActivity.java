package de.symeda.sormas.app.caze.list;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.Date;
import java.util.Random;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.app.BaseListActivity;
import de.symeda.sormas.app.BaseListActivityFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.caze.edit.CaseNewActivity;
import de.symeda.sormas.app.component.dialog.MissingWeeklyReportDialog;
import de.symeda.sormas.app.component.dialog.TeboAlertDialogInterface;
import de.symeda.sormas.app.component.menu.LandingPageMenuItem;
import de.symeda.sormas.app.core.IListNavigationCapsule;
import de.symeda.sormas.app.core.ListNavigationCapsule;
import de.symeda.sormas.app.core.SearchBy;
import de.symeda.sormas.app.shared.CaseFormNavigationCapsule;
import de.symeda.sormas.app.util.MenuOptionsHelper;

/**
 * Created by Orson on 05/12/2017.
 */

public class CaseListActivity extends BaseListActivity {

    private final int DATA_XML_PAGE_MENU = R.xml.data_landing_page_case_menu;// "xml/data_landing_page_case_menu.xml";

    private static final int MENU_INDEX_CASE_PENDING = 0;
    private static final int MENU_INDEX_CASE_DONE = 1;
    private static final int MENU_INDEX_CASE_DISCARDED = 2;

    private InvestigationStatus statusFilters[] = new InvestigationStatus[] { InvestigationStatus.PENDING, InvestigationStatus.DONE, InvestigationStatus.DISCARDED };

    private AsyncTask jobTask;
    private InvestigationStatus filterStatus = null;
    private SearchBy searchBy = null;
    private String recordUuid = null;
    private BaseListActivityFragment activeFragment = null;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        SaveFilterStatusState(outState, filterStatus);
        SaveSearchStrategyState(outState, searchBy);
        SaveRecordUuidState(outState, recordUuid);
    }

    @Override
    protected void initializeActivity(Bundle arguments) {
        filterStatus = (InvestigationStatus) getFilterStatusArg(arguments);
        searchBy = (SearchBy) getSearchStrategyArg(arguments);
        recordUuid = getRecordUuidArg(arguments);
    }

    @Override
    public BaseListActivityFragment getActiveReadFragment() throws IllegalAccessException, InstantiationException {
        if (activeFragment == null) {
            IListNavigationCapsule dataCapsule = new ListNavigationCapsule(CaseListActivity.this, filterStatus, searchBy);
            activeFragment = CaseListFragment.newInstance(this, dataCapsule);
        }

        return activeFragment;
    }

    @Override
    public int getPageMenuData() {
        return DATA_XML_PAGE_MENU;
    }

    @Override
    public int onNotificationCountChangingAsync(AdapterView parent, LandingPageMenuItem menuItem, int position) {
        //TODO: Call database and retrieve notification count
        return new Random().nextInt(100);
        //return (int)(new Random(DateTime.now().getMillis() * 1000).nextInt()/10000000);
    }

    @Override
    protected BaseListActivityFragment getNextFragment(LandingPageMenuItem menuItem) {
        InvestigationStatus status = statusFilters[menuItem.getKey()];

        if (status == null)
            return null;

        filterStatus = status;
        IListNavigationCapsule dataCapsule = new ListNavigationCapsule(CaseListActivity.this, filterStatus, searchBy);

        try {
            activeFragment = CaseListFragment.newInstance(this, dataCapsule);
        } catch (InstantiationException e) {
            Log.e(TAG, e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        }

        return activeFragment;
    }

    @Override
    public Enum getStatus() {
        return null;
    }

    @Override
    public boolean showStatusFrame() {
        return false;
    }

    @Override
    public boolean showTitleBar() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getNewMenu().setTitle(R.string.action_new_case);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MenuOptionsHelper.handleListModuleOptionsItemSelected(this, item))
            return super.onOptionsItemSelected(item);

        return true;
    }

    @Override
    protected int getActivityTitle() {
        return R.string.heading_level2_cases_list;
    }

    @Override
    public void gotoNewView() {
        EpiWeek lastEpiWeek = DateHelper.getPreviousEpiWeek(new Date());
        User user = ConfigProvider.getUser();
        if (user.hasUserRole(UserRole.INFORMANT)
                && DatabaseHelper.getWeeklyReportDao().queryForEpiWeek(lastEpiWeek, ConfigProvider.getUser()) == null) {

            MissingWeeklyReportDialog confirmationDialog = new MissingWeeklyReportDialog(this);

            confirmationDialog.setOnPositiveClickListener(new TeboAlertDialogInterface.PositiveOnClickListener() {
                @Override
                public void onOkClick(View v, Object item, View viewRoot) {
                    /*Intent intent = new Intent(CaseListActivity.this, ReportsActivity.class);
                    startActivity(intent);*/
                }
            });

            confirmationDialog.show(null);
        } else {
            CaseFormNavigationCapsule dataCapsule = (CaseFormNavigationCapsule)new CaseFormNavigationCapsule(CaseListActivity.this,
                    null).setEditPageStatus(filterStatus).setPersonUuid(null);
            CaseNewActivity.goToActivity(this, dataCapsule);
        }
    }

    public static void goToActivity(Context fromActivity, IListNavigationCapsule dataCapsule) {
        BaseListActivity.goToActivity(fromActivity, CaseListActivity.class, dataCapsule);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (activeFragment != null)
            activeFragment.cancelTaskExec();
    }
}