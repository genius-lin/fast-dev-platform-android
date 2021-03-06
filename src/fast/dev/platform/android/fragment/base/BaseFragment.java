package fast.dev.platform.android.fragment.base;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;
import com.testin.agent.TestinAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import fast.dev.platform.android.bean.base.BaseBusinessBean;
import fast.dev.platform.android.http.volley.VolleyWrapper;
import fast.dev.platform.android.imageloader.PicassoImageLoader;
import fast.dev.platform.android.util.CommonUtils;
import fast.dev.platform.android.util.ToastUtils;

public abstract class BaseFragment extends Fragment implements OnClickListener, ErrorListener {

	protected Gson gson = new Gson();
	
	protected VolleyWrapper volleyWrapper;
	
	protected SharedPreferences user_sp;// 用户信息数据库
	protected SharedPreferences dict_sp;// 字典信息数据库
	protected SharedPreferences sys_sp;// 系统信息数据库
	protected SharedPreferences location_sp;// 定位信息数据库
	
	protected PicassoImageLoader mImageLoader;// 图片加载器-Picasso

	public Context getContext() {
		return this.getActivity();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mImageLoader = new PicassoImageLoader(getContext());
		
		user_sp = CommonUtils.user_sp(getContext());
		dict_sp = CommonUtils.dict_sp(getContext());
		sys_sp = CommonUtils.sys_sp(getContext());
		location_sp = CommonUtils.location_sp(getContext());
		
		volleyWrapper = new VolleyWrapper(getContext());
	}

	/**
	 * 处理请求的返回信息
	 * 
	 * @param businessBean
	 * 
	 * @return
	 */
	public boolean handleRequestResult(BaseBusinessBean businessBean) {
		return CommonUtils.handleRequestResult(getContext(), businessBean);
	}
	
	/**
	 * 处理请求的返回信息
	 * 
	 * @param businessBean
	 * @param hideErrorInfo 隐藏错误提示信息
	 * @param showDefaultErrorInfo 显示默认错误提示信息
	 * @param customMsg 自定义错误提示消息
	 * 
	 * @return
	 */
	public boolean handleRequestResult(BaseBusinessBean businessBean, boolean hideErrorInfo, boolean showDefaultErrorInfo, String customMsg) {
		return CommonUtils.handleRequestResult(getContext(), businessBean, hideErrorInfo, showDefaultErrorInfo, customMsg);
	}
	
	/**
	 * 关闭当前Activity
	 */
	protected void finishActivity() {
		getActivity().finish();
	}
	
	@Override
	public void onClick(View v) {
		
	}
	
	@Override
	public void onDetach() {
		super.onDetach();

		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	protected void post(boolean showLoadingDialog, String url, HashMap<String, String> params,
			Listener<String> listener, ErrorListener errorListener, int retryNum) {
		if (showLoadingDialog) {
			CommonUtils.showLoadingProgressDialog(getContext());
		}
		volleyWrapper.post(url, params, listener, errorListener, retryNum);
	}

	protected void post(boolean showLoadingDialog, String url, HashMap<String, String> params,
			Listener<String> listener, ErrorListener errorListener) {
		post(showLoadingDialog, url, params, listener, errorListener, 0);
	}
	
	protected void post(boolean showLoadingDialog, String url, HashMap<String, String> params,
			Listener<String> listener, int retryNum) {
		post(showLoadingDialog, url, params, listener, this, retryNum);
	}
	
	protected void post(boolean showLoadingDialog, String url, HashMap<String, String> params,
			Listener<String> listener) {
		post(showLoadingDialog, url, params, listener, this, 0);
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		CommonUtils.dismissProgressDialog();

		ToastUtils.showShort(getContext(), "网络异常，请稍后重试。");

		CrashReport.postCatchedException(error);

		TestinAgent.uploadException(getContext(), "调用服务器端接口报错", error);
	}
	
}
