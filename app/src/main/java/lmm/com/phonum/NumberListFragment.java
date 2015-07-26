package lmm.com.phonum;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import lmm.com.phonum.utils.CallListUtils;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class NumberListFragment extends android.support.v4.app.Fragment implements AbsListView.OnItemClickListener {

    private static final String CALL_CATEGORY = "call_category";

    private String callCategory;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private NumberListAdapter mAdapter;

    private CallListUtils.Number item1;

    private NumberListLoader mLoader;

    public static NumberListFragment newInstance(String param) {
        NumberListFragment fragment = new NumberListFragment();
        Bundle args = new Bundle();
        args.putString(CALL_CATEGORY, param);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NumberListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            callCategory = getArguments().getString(CALL_CATEGORY);
        }

        mAdapter = new NumberListAdapter(getActivity());

        mLoader = new NumberListLoader();
        mLoader.execute(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_numberlist, container, false);

        // Set the adapter
        mListView = (ListView)view.findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        mListView.setItemsCanFocus(true);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener.returnToolbarHomeState();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.showNumber(mAdapter.getItem(position), position);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public void showSearchResults(String query){
        mListView.setAdapter(mAdapter.search(query));
    }

    public void reset_search(){
        mListView.setAdapter(mAdapter);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void showNumber(CallListUtils.Number number, int position);
        public void returnToolbarHomeState();
    }

    private class NumberListLoader extends AsyncTask<Context, Void, List<CallListUtils.Number>>{
        @Override
        protected List<CallListUtils.Number> doInBackground(Context... params) {
            List<CallListUtils.Number> numbers = CallListUtils.refreshNumbers(params[0]);
            return numbers;
        }

        @Override
        protected void onPostExecute(List<CallListUtils.Number> numbers) {
            super.onPostExecute(numbers);
            mAdapter.addAll(numbers);
        }
    }

}
