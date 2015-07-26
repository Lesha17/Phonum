package lmm.com.phonum;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import lmm.com.phonum.utils.CallListUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NumberInfoFrgment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NumberInfoFrgment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NumberInfoFrgment extends android.support.v4.app.Fragment {
    private static final String NUMBER_PARAM = "number";

    private OnFragmentInteractionListener mListener;

    private CallListUtils.Number number;

    private TextView display_name;
    private TextView phoneNumber;
    private TextView category;
    private TextView hint;
    private View header;
    private ListView call_list;

    private CallListAdapter adapter;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param number a CallListUtils.Number object which info have to be shown.
     * @return A new instance of fragment NumberInfoFrgment.
     */
    public static NumberInfoFrgment newInstance(CallListUtils.Number number) {
        NumberInfoFrgment fragment = new NumberInfoFrgment();
        Bundle args = new Bundle();
        args.putSerializable(NUMBER_PARAM, number);
        fragment.setArguments(args);
        return fragment;
    }

    public NumberInfoFrgment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.number = (CallListUtils.Number)getArguments().getSerializable(NUMBER_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_info, container, false);

        call_list = (ListView)view.findViewById(R.id.calls_list);

        header = getActivity().getLayoutInflater().inflate(R.layout.number_info_header, null);

        display_name = (TextView)header.findViewById(R.id.display_name);
        phoneNumber = (TextView)header.findViewById(R.id.phone_number);
        category = (TextView)header.findViewById(R.id.category);
        hint = (TextView)header.findViewById(R.id.hint);

        if(number.hasName){
            display_name.setText(number.name);
            phoneNumber.setText(number.formatted_number);
        } else {
            display_name.setText(number.formatted_number);
        }
        phoneNumber.setSelected(true);

        call_list.addHeaderView(header, null, false);

        adapter = new CallListAdapter(getActivity());
        adapter.addAll(number.calls);
        call_list.setAdapter(adapter);

        Log.d("Calls size", number.calls.size() + "");
        Log.d("Adapter size", adapter.getCount() + "");

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener.provideBackNavigation();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_number_info, menu);
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
        public void provideBackNavigation();
    }

}
