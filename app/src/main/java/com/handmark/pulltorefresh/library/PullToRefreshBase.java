package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.handmark.pulltorefresh.library.internal.Utils;
import com.MCWorld.framework.R;

public abstract class PullToRefreshBase<T extends View> extends LinearLayout implements IPullToRefresh<T> {
    static final boolean DEBUG = false;
    static final int DEMO_SCROLL_INTERVAL = 225;
    static final float FRICTION = 2.0f;
    static final String LOG_TAG = "PullToRefresh";
    public static final int SMOOTH_SCROLL_DURATION_MS = 200;
    public static final int SMOOTH_SCROLL_LONG_DURATION_MS = 325;
    static final String STATE_CURRENT_MODE = "ptr_current_mode";
    static final String STATE_MODE = "ptr_mode";
    static final String STATE_SCROLLING_REFRESHING_ENABLED = "ptr_disable_scrolling";
    static final String STATE_SHOW_REFRESHING_VIEW = "ptr_show_refreshing_view";
    static final String STATE_STATE = "ptr_state";
    static final String STATE_SUPER = "ptr_super";
    static final boolean USE_HW_LAYERS = false;
    private Mode mCurrentMode;
    private SmoothScrollRunnable mCurrentSmoothScrollRunnable;
    private boolean mFilterTouchEvents = true;
    private LoadingLayout mFooterLayout;
    private LoadingLayout mHeaderLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private boolean mIsBeingDragged = false;
    private float mLastMotionX;
    private float mLastMotionY;
    private boolean mLayoutVisibilityChangesEnabled = true;
    private AnimationStyle mLoadingAnimationStyle = AnimationStyle.getDefault();
    private Mode mMode = Mode.getDefault();
    private OnPullEventListener<T> mOnPullEventListener;
    private OnRefreshListener<T> mOnRefreshListener;
    private OnRefreshListener2<T> mOnRefreshListener2;
    private boolean mOverScrollEnabled = true;
    protected T mRefreshableView;
    private FrameLayout mRefreshableViewWrapper;
    private Interpolator mScrollAnimationInterpolator;
    private boolean mScrollingWhileRefreshingEnabled = false;
    private boolean mShowViewWhileRefreshing = true;
    private State mState = State.RESET;
    private int mTouchSlop;

    public interface OnRefreshListener<V extends View> {
        void onRefresh(PullToRefreshBase<V> pullToRefreshBase);
    }

    protected abstract T createRefreshableView(Context context, AttributeSet attributeSet);

    public abstract Orientation getPullToRefreshScrollDirection();

    protected abstract boolean isReadyForPullEnd();

    protected abstract boolean isReadyForPullStart();

    public PullToRefreshBase(Context context) {
        super(context);
        init(context, null);
    }

    public PullToRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PullToRefreshBase(Context context, Mode mode) {
        super(context);
        this.mMode = mode;
        init(context, null);
    }

    public PullToRefreshBase(Context context, Mode mode, AnimationStyle animStyle) {
        super(context);
        this.mMode = mode;
        this.mLoadingAnimationStyle = animStyle;
        init(context, null);
    }

    public void addView(View child, int index, LayoutParams params) {
        T refreshableView = getRefreshableView();
        if (refreshableView instanceof ViewGroup) {
            ((ViewGroup) refreshableView).addView(child, index, params);
            return;
        }
        throw new UnsupportedOperationException("Refreshable View is not a_isRightVersion ViewGroup so can't addView");
    }

    public final boolean demo() {
        if (this.mMode.showHeaderLoadingLayout() && isReadyForPullStart()) {
            smoothScrollToAndBack((-getHeaderSize()) * 2);
            return true;
        } else if (!this.mMode.showFooterLoadingLayout() || !isReadyForPullEnd()) {
            return false;
        } else {
            smoothScrollToAndBack(getFooterSize() * 2);
            return true;
        }
    }

    public final Mode getCurrentMode() {
        return this.mCurrentMode;
    }

    public final boolean getFilterTouchEvents() {
        return this.mFilterTouchEvents;
    }

    public final ILoadingLayout getLoadingLayoutProxy() {
        return getLoadingLayoutProxy(true, true);
    }

    public final ILoadingLayout getLoadingLayoutProxy(boolean includeStart, boolean includeEnd) {
        return createLoadingLayoutProxy(includeStart, includeEnd);
    }

    public final Mode getMode() {
        return this.mMode;
    }

    public final T getRefreshableView() {
        return this.mRefreshableView;
    }

    public final boolean getShowViewWhileRefreshing() {
        return this.mShowViewWhileRefreshing;
    }

    public final State getState() {
        return this.mState;
    }

    public final boolean isDisableScrollingWhileRefreshing() {
        return !isScrollingWhileRefreshingEnabled();
    }

    public final boolean isPullToRefreshEnabled() {
        return this.mMode.permitsPullToRefresh();
    }

    public final boolean isPullToRefreshOverScrollEnabled() {
        return VERSION.SDK_INT >= 9 && this.mOverScrollEnabled && OverscrollHelper.isAndroidOverScrollEnabled(this.mRefreshableView);
    }

    public final boolean isRefreshing() {
        return this.mState == State.REFRESHING || this.mState == State.MANUAL_REFRESHING;
    }

    public final boolean isScrollingWhileRefreshingEnabled() {
        return this.mScrollingWhileRefreshingEnabled;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!isPullToRefreshEnabled()) {
            return false;
        }
        int action = event.getAction();
        if (action == 3 || action == 1) {
            this.mIsBeingDragged = false;
            return false;
        } else if (action != 0 && this.mIsBeingDragged) {
            return true;
        } else {
            switch (action) {
                case 0:
                    if (isReadyForPull()) {
                        float y = event.getY();
                        this.mInitialMotionY = y;
                        this.mLastMotionY = y;
                        y = event.getX();
                        this.mInitialMotionX = y;
                        this.mLastMotionX = y;
                        this.mIsBeingDragged = false;
                        break;
                    }
                    break;
                case 2:
                    if (this.mScrollingWhileRefreshingEnabled || !isRefreshing()) {
                        if (isReadyForPull()) {
                            float diff;
                            float y2 = event.getY();
                            float x = event.getX();
                            float oppositeDiff;
                            switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Orientation[getPullToRefreshScrollDirection().ordinal()]) {
                                case 1:
                                    diff = x - this.mLastMotionX;
                                    oppositeDiff = y2 - this.mLastMotionY;
                                    break;
                                default:
                                    diff = y2 - this.mLastMotionY;
                                    oppositeDiff = x - this.mLastMotionX;
                                    break;
                            }
                            float absDiff = Math.abs(diff);
                            if (absDiff > ((float) this.mTouchSlop) && (!this.mFilterTouchEvents || absDiff > Math.abs(oppositeDiff))) {
                                if (!this.mMode.showHeaderLoadingLayout() || diff < 1.0f || !isReadyForPullStart()) {
                                    if (this.mMode.showFooterLoadingLayout() && diff <= -1.0f && isReadyForPullEnd()) {
                                        this.mLastMotionY = y2;
                                        this.mLastMotionX = x;
                                        this.mIsBeingDragged = true;
                                        if (this.mMode == Mode.BOTH) {
                                            this.mCurrentMode = Mode.PULL_FROM_END;
                                            break;
                                        }
                                    }
                                }
                                this.mLastMotionY = y2;
                                this.mLastMotionX = x;
                                this.mIsBeingDragged = true;
                                if (this.mMode == Mode.BOTH) {
                                    this.mCurrentMode = Mode.PULL_FROM_START;
                                    break;
                                }
                            }
                        }
                    }
                    return true;
                    break;
            }
            return this.mIsBeingDragged;
        }
    }

    public final void onRefreshComplete() {
        if (isRefreshing()) {
            setState(State.RESET, new boolean[0]);
        }
    }

    public final boolean onTouchEvent(MotionEvent event) {
        if (!isPullToRefreshEnabled()) {
            return false;
        }
        if (!this.mScrollingWhileRefreshingEnabled && isRefreshing()) {
            return true;
        }
        if (event.getAction() == 0 && event.getEdgeFlags() != 0) {
            return false;
        }
        switch (event.getAction()) {
            case 0:
                if (!isReadyForPull()) {
                    return false;
                }
                float y = event.getY();
                this.mInitialMotionY = y;
                this.mLastMotionY = y;
                y = event.getX();
                this.mInitialMotionX = y;
                this.mLastMotionX = y;
                return true;
            case 1:
            case 3:
                if (!this.mIsBeingDragged) {
                    return false;
                }
                this.mIsBeingDragged = false;
                if (this.mState == State.RELEASE_TO_REFRESH && (this.mOnRefreshListener != null || this.mOnRefreshListener2 != null)) {
                    setState(State.REFRESHING, true);
                    return true;
                } else if (isRefreshing()) {
                    smoothScrollTo(0);
                    return true;
                } else {
                    setState(State.RESET, new boolean[0]);
                    return true;
                }
            case 2:
                if (!this.mIsBeingDragged) {
                    return false;
                }
                this.mLastMotionY = event.getY();
                this.mLastMotionX = event.getX();
                pullEvent();
                return true;
            default:
                return false;
        }
    }

    public final void setScrollingWhileRefreshingEnabled(boolean allowScrollingWhileRefreshing) {
        this.mScrollingWhileRefreshingEnabled = allowScrollingWhileRefreshing;
    }

    public void setDisableScrollingWhileRefreshing(boolean disableScrollingWhileRefreshing) {
        setScrollingWhileRefreshingEnabled(!disableScrollingWhileRefreshing);
    }

    public final void setFilterTouchEvents(boolean filterEvents) {
        this.mFilterTouchEvents = filterEvents;
    }

    public void setLastUpdatedLabel(CharSequence label) {
        getLoadingLayoutProxy().setLastUpdatedLabel(label);
    }

    public void setLoadingDrawable(Drawable drawable) {
        getLoadingLayoutProxy().setLoadingDrawable(drawable);
    }

    public void setLoadingDrawable(Drawable drawable, Mode mode) {
        getLoadingLayoutProxy(mode.showHeaderLoadingLayout(), mode.showFooterLoadingLayout()).setLoadingDrawable(drawable);
    }

    public void setLongClickable(boolean longClickable) {
        getRefreshableView().setLongClickable(longClickable);
    }

    public final void setMode(Mode mode) {
        if (mode != this.mMode) {
            this.mMode = mode;
            updateUIForMode();
        }
    }

    public void setOnPullEventListener(OnPullEventListener<T> listener) {
        this.mOnPullEventListener = listener;
    }

    public final void setOnRefreshListener(OnRefreshListener<T> listener) {
        this.mOnRefreshListener = listener;
        this.mOnRefreshListener2 = null;
    }

    public final void setOnRefreshListener(OnRefreshListener2<T> listener) {
        this.mOnRefreshListener2 = listener;
        this.mOnRefreshListener = null;
    }

    public void setPullLabel(CharSequence pullLabel) {
        getLoadingLayoutProxy().setPullLabel(pullLabel);
    }

    public void setPullLabel(CharSequence pullLabel, Mode mode) {
        getLoadingLayoutProxy(mode.showHeaderLoadingLayout(), mode.showFooterLoadingLayout()).setPullLabel(pullLabel);
    }

    public final void setPullToRefreshEnabled(boolean enable) {
        setMode(enable ? Mode.getDefault() : Mode.DISABLED);
    }

    public final void setPullToRefreshOverScrollEnabled(boolean enabled) {
        this.mOverScrollEnabled = enabled;
    }

    public final void setRefreshing() {
        setRefreshing(true);
    }

    public final void setRefreshing(boolean doScroll) {
        if (!isRefreshing()) {
            setState(State.MANUAL_REFRESHING, doScroll);
        }
    }

    public void setRefreshingLabel(CharSequence refreshingLabel) {
        getLoadingLayoutProxy().setRefreshingLabel(refreshingLabel);
    }

    public void setRefreshingLabel(CharSequence refreshingLabel, Mode mode) {
        getLoadingLayoutProxy(mode.showHeaderLoadingLayout(), mode.showFooterLoadingLayout()).setRefreshingLabel(refreshingLabel);
    }

    public void setReleaseLabel(CharSequence releaseLabel) {
        setReleaseLabel(releaseLabel, Mode.BOTH);
    }

    public void setReleaseLabel(CharSequence releaseLabel, Mode mode) {
        getLoadingLayoutProxy(mode.showHeaderLoadingLayout(), mode.showFooterLoadingLayout()).setReleaseLabel(releaseLabel);
    }

    public void setScrollAnimationInterpolator(Interpolator interpolator) {
        this.mScrollAnimationInterpolator = interpolator;
    }

    public final void setShowViewWhileRefreshing(boolean showView) {
        this.mShowViewWhileRefreshing = showView;
    }

    final void setState(State state, boolean... params) {
        this.mState = state;
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$State[this.mState.ordinal()]) {
            case 1:
                onReset();
                break;
            case 2:
                onPullToRefresh();
                break;
            case 3:
                onReleaseToRefresh();
                break;
            case 4:
            case 5:
                onRefreshing(params[0]);
                break;
        }
        if (this.mOnPullEventListener != null) {
            this.mOnPullEventListener.onPullEvent(this, this.mState, this.mCurrentMode);
        }
    }

    protected final void addViewInternal(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
    }

    protected final void addViewInternal(View child, LayoutParams params) {
        super.addView(child, -1, params);
    }

    protected LoadingLayout createLoadingLayout(Context context, Mode mode, TypedArray attrs) {
        LoadingLayout layout = this.mLoadingAnimationStyle.createLoadingLayout(context, mode, getPullToRefreshScrollDirection(), attrs);
        layout.setVisibility(4);
        return layout;
    }

    protected LoadingLayoutProxy createLoadingLayoutProxy(boolean includeStart, boolean includeEnd) {
        LoadingLayoutProxy proxy = new LoadingLayoutProxy();
        if (includeStart && this.mMode.showHeaderLoadingLayout()) {
            proxy.addLayout(this.mHeaderLayout);
        }
        if (includeEnd && this.mMode.showFooterLoadingLayout()) {
            proxy.addLayout(this.mFooterLayout);
        }
        return proxy;
    }

    protected final void disableLoadingLayoutVisibilityChanges() {
        this.mLayoutVisibilityChangesEnabled = false;
    }

    protected final LoadingLayout getFooterLayout() {
        return this.mFooterLayout;
    }

    protected final int getFooterSize() {
        return this.mFooterLayout.getContentSize();
    }

    protected final LoadingLayout getHeaderLayout() {
        return this.mHeaderLayout;
    }

    protected final int getHeaderSize() {
        return this.mHeaderLayout.getContentSize();
    }

    protected int getPullToRefreshScrollDuration() {
        return 200;
    }

    protected int getPullToRefreshScrollDurationLonger() {
        return 325;
    }

    protected FrameLayout getRefreshableViewWrapper() {
        return this.mRefreshableViewWrapper;
    }

    protected void handleStyledAttributes(TypedArray a) {
    }

    protected void onPtrRestoreInstanceState(Bundle savedInstanceState) {
    }

    protected void onPtrSaveInstanceState(Bundle saveState) {
    }

    protected void onPullToRefresh() {
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Mode[this.mCurrentMode.ordinal()]) {
            case 1:
                this.mFooterLayout.pullToRefresh();
                return;
            case 2:
                this.mHeaderLayout.pullToRefresh();
                return;
            default:
                return;
        }
    }

    protected void onRefreshing(boolean doScroll) {
        if (this.mMode.showHeaderLoadingLayout()) {
            this.mHeaderLayout.refreshing();
        }
        if (this.mMode.showFooterLoadingLayout()) {
            this.mFooterLayout.refreshing();
        }
        if (!doScroll) {
            callRefreshListener();
        } else if (this.mShowViewWhileRefreshing) {
            OnSmoothScrollFinishedListener listener = new 1(this);
            switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Mode[this.mCurrentMode.ordinal()]) {
                case 1:
                case 3:
                    smoothScrollTo(getFooterSize(), listener);
                    return;
                default:
                    smoothScrollTo(-getHeaderSize(), listener);
                    return;
            }
        } else {
            smoothScrollTo(0);
        }
    }

    protected void onReleaseToRefresh() {
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Mode[this.mCurrentMode.ordinal()]) {
            case 1:
                this.mFooterLayout.releaseToRefresh();
                return;
            case 2:
                this.mHeaderLayout.releaseToRefresh();
                return;
            default:
                return;
        }
    }

    protected void onReset() {
        this.mIsBeingDragged = false;
        this.mLayoutVisibilityChangesEnabled = true;
        this.mHeaderLayout.reset();
        this.mFooterLayout.reset();
        smoothScrollTo(0);
    }

    protected final void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            setMode(Mode.mapIntToValue(bundle.getInt(STATE_MODE, 0)));
            this.mCurrentMode = Mode.mapIntToValue(bundle.getInt(STATE_CURRENT_MODE, 0));
            this.mScrollingWhileRefreshingEnabled = bundle.getBoolean(STATE_SCROLLING_REFRESHING_ENABLED, false);
            this.mShowViewWhileRefreshing = bundle.getBoolean(STATE_SHOW_REFRESHING_VIEW, true);
            super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER));
            State viewState = State.mapIntToValue(bundle.getInt(STATE_STATE, 0));
            if (viewState == State.REFRESHING || viewState == State.MANUAL_REFRESHING) {
                setState(viewState, true);
            }
            onPtrRestoreInstanceState(bundle);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    protected final Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        onPtrSaveInstanceState(bundle);
        bundle.putInt(STATE_STATE, this.mState.getIntValue());
        bundle.putInt(STATE_MODE, this.mMode.getIntValue());
        bundle.putInt(STATE_CURRENT_MODE, this.mCurrentMode.getIntValue());
        bundle.putBoolean(STATE_SCROLLING_REFRESHING_ENABLED, this.mScrollingWhileRefreshingEnabled);
        bundle.putBoolean(STATE_SHOW_REFRESHING_VIEW, this.mShowViewWhileRefreshing);
        bundle.putParcelable(STATE_SUPER, super.onSaveInstanceState());
        return bundle;
    }

    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshLoadingViewsSize();
        refreshRefreshableViewSize(w, h);
        post(new 2(this));
    }

    protected final void refreshLoadingViewsSize() {
        int maximumPullScroll = (int) (((float) getMaximumPullScroll()) * 1.2f);
        int pLeft = getPaddingLeft();
        int pTop = getPaddingTop();
        int pRight = getPaddingRight();
        int pBottom = getPaddingBottom();
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Orientation[getPullToRefreshScrollDirection().ordinal()]) {
            case 1:
                if (this.mMode.showHeaderLoadingLayout()) {
                    this.mHeaderLayout.setWidth(maximumPullScroll);
                    pLeft = -maximumPullScroll;
                } else {
                    pLeft = 0;
                }
                if (!this.mMode.showFooterLoadingLayout()) {
                    pRight = 0;
                    break;
                }
                this.mFooterLayout.setWidth(maximumPullScroll);
                pRight = -maximumPullScroll;
                break;
            case 2:
                if (this.mMode.showHeaderLoadingLayout()) {
                    this.mHeaderLayout.setHeight(maximumPullScroll);
                    pTop = -maximumPullScroll;
                } else {
                    pTop = 0;
                }
                if (!this.mMode.showFooterLoadingLayout()) {
                    pBottom = 0;
                    break;
                }
                this.mFooterLayout.setHeight(maximumPullScroll);
                pBottom = -maximumPullScroll;
                break;
        }
        setPadding(pLeft, pTop, pRight, pBottom);
    }

    protected final void refreshRefreshableViewSize(int width, int height) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.mRefreshableViewWrapper.getLayoutParams();
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Orientation[getPullToRefreshScrollDirection().ordinal()]) {
            case 1:
                if (lp.width != width) {
                    lp.width = width;
                    this.mRefreshableViewWrapper.requestLayout();
                    return;
                }
                return;
            case 2:
                if (lp.height != height) {
                    lp.height = height;
                    this.mRefreshableViewWrapper.requestLayout();
                    return;
                }
                return;
            default:
                return;
        }
    }

    protected final void setHeaderScroll(int value) {
        int maximumPullScroll = getMaximumPullScroll();
        value = Math.min(maximumPullScroll, Math.max(-maximumPullScroll, value));
        if (this.mLayoutVisibilityChangesEnabled) {
            if (value < 0) {
                this.mHeaderLayout.setVisibility(0);
            } else if (value > 0) {
                this.mFooterLayout.setVisibility(0);
            } else {
                this.mHeaderLayout.setVisibility(4);
                this.mFooterLayout.setVisibility(4);
            }
        }
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Orientation[getPullToRefreshScrollDirection().ordinal()]) {
            case 1:
                scrollTo(value, 0);
                return;
            case 2:
                scrollTo(0, value);
                return;
            default:
                return;
        }
    }

    protected final void smoothScrollTo(int scrollValue) {
        smoothScrollTo(scrollValue, (long) getPullToRefreshScrollDuration());
    }

    protected final void smoothScrollTo(int scrollValue, OnSmoothScrollFinishedListener listener) {
        smoothScrollTo(scrollValue, (long) getPullToRefreshScrollDuration(), 0, listener);
    }

    protected final void smoothScrollToLonger(int scrollValue) {
        smoothScrollTo(scrollValue, (long) getPullToRefreshScrollDurationLonger());
    }

    protected void updateUIForMode() {
        LinearLayout.LayoutParams lp = getLoadingLayoutLayoutParams();
        if (this == this.mHeaderLayout.getParent()) {
            removeView(this.mHeaderLayout);
        }
        if (this.mMode.showHeaderLoadingLayout()) {
            addViewInternal(this.mHeaderLayout, 0, lp);
        }
        if (this == this.mFooterLayout.getParent()) {
            removeView(this.mFooterLayout);
        }
        if (this.mMode.showFooterLoadingLayout()) {
            addViewInternal(this.mFooterLayout, lp);
        }
        refreshLoadingViewsSize();
        this.mCurrentMode = this.mMode != Mode.BOTH ? this.mMode : Mode.PULL_FROM_START;
    }

    private void addRefreshableView(Context context, T refreshableView) {
        this.mRefreshableViewWrapper = new FrameLayout(context);
        this.mRefreshableViewWrapper.addView(refreshableView, -1, -1);
        addViewInternal(this.mRefreshableViewWrapper, new LinearLayout.LayoutParams(-1, -1));
    }

    private void callRefreshListener() {
        if (this.mOnRefreshListener != null) {
            this.mOnRefreshListener.onRefresh(this);
        } else if (this.mOnRefreshListener2 == null) {
        } else {
            if (this.mCurrentMode == Mode.PULL_FROM_START) {
                this.mOnRefreshListener2.onPullDownToRefresh(this);
            } else if (this.mCurrentMode == Mode.PULL_FROM_END) {
                this.mOnRefreshListener2.onPullUpToRefresh(this);
            }
        }
    }

    private void init(Context context, AttributeSet attrs) {
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Orientation[getPullToRefreshScrollDirection().ordinal()]) {
            case 1:
                setOrientation(0);
                break;
            default:
                setOrientation(1);
                break;
        }
        setGravity(17);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefresh);
        if (a.hasValue(R.styleable.PullToRefresh_ptrMode)) {
            this.mMode = Mode.mapIntToValue(a.getInteger(R.styleable.PullToRefresh_ptrMode, 0));
        }
        if (a.hasValue(R.styleable.PullToRefresh_ptrAnimationStyle)) {
            this.mLoadingAnimationStyle = AnimationStyle.mapIntToValue(a.getInteger(R.styleable.PullToRefresh_ptrAnimationStyle, 0));
        }
        this.mRefreshableView = createRefreshableView(context, attrs);
        addRefreshableView(context, this.mRefreshableView);
        this.mHeaderLayout = createLoadingLayout(context, Mode.PULL_FROM_START, a);
        this.mFooterLayout = createLoadingLayout(context, Mode.PULL_FROM_END, a);
        Drawable background;
        if (a.hasValue(R.styleable.PullToRefresh_ptrRefreshableViewBackground)) {
            background = a.getDrawable(R.styleable.PullToRefresh_ptrRefreshableViewBackground);
            if (background != null) {
                this.mRefreshableView.setBackgroundDrawable(background);
            }
        } else if (a.hasValue(R.styleable.PullToRefresh_ptrAdapterViewBackground)) {
            Utils.warnDeprecation("ptrAdapterViewBackground", "ptrRefreshableViewBackground");
            background = a.getDrawable(R.styleable.PullToRefresh_ptrAdapterViewBackground);
            if (background != null) {
                this.mRefreshableView.setBackgroundDrawable(background);
            }
        }
        if (a.hasValue(R.styleable.PullToRefresh_ptrOverScroll)) {
            this.mOverScrollEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrOverScroll, true);
        }
        if (a.hasValue(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
            this.mScrollingWhileRefreshingEnabled = a.getBoolean(R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled, false);
        }
        handleStyledAttributes(a);
        a.recycle();
        updateUIForMode();
    }

    private boolean isReadyForPull() {
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Mode[this.mMode.ordinal()]) {
            case 1:
                return isReadyForPullEnd();
            case 2:
                return isReadyForPullStart();
            case 4:
                return isReadyForPullEnd() || isReadyForPullStart();
            default:
                return false;
        }
    }

    private void pullEvent() {
        float initialMotionValue;
        float lastMotionValue;
        int newScrollValue;
        int itemDimension;
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Orientation[getPullToRefreshScrollDirection().ordinal()]) {
            case 1:
                initialMotionValue = this.mInitialMotionX;
                lastMotionValue = this.mLastMotionX;
                break;
            default:
                initialMotionValue = this.mInitialMotionY;
                lastMotionValue = this.mLastMotionY;
                break;
        }
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Mode[this.mCurrentMode.ordinal()]) {
            case 1:
                newScrollValue = Math.round(Math.max(initialMotionValue - lastMotionValue, 0.0f) / FRICTION);
                itemDimension = getFooterSize();
                break;
            default:
                newScrollValue = Math.round(Math.min(initialMotionValue - lastMotionValue, 0.0f) / FRICTION);
                itemDimension = getHeaderSize();
                break;
        }
        setHeaderScroll(newScrollValue);
        if (newScrollValue != 0 && !isRefreshing()) {
            float scale = ((float) Math.abs(newScrollValue)) / ((float) itemDimension);
            switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Mode[this.mCurrentMode.ordinal()]) {
                case 1:
                    this.mFooterLayout.onPull(scale);
                    break;
                default:
                    this.mHeaderLayout.onPull(scale);
                    break;
            }
            if (this.mState != State.PULL_TO_REFRESH && itemDimension >= Math.abs(newScrollValue)) {
                setState(State.PULL_TO_REFRESH, new boolean[0]);
            } else if (this.mState == State.PULL_TO_REFRESH && itemDimension < Math.abs(newScrollValue)) {
                setState(State.RELEASE_TO_REFRESH, new boolean[0]);
            }
        }
    }

    private LinearLayout.LayoutParams getLoadingLayoutLayoutParams() {
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Orientation[getPullToRefreshScrollDirection().ordinal()]) {
            case 1:
                return new LinearLayout.LayoutParams(-2, -1);
            default:
                return new LinearLayout.LayoutParams(-1, -2);
        }
    }

    private int getMaximumPullScroll() {
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Orientation[getPullToRefreshScrollDirection().ordinal()]) {
            case 1:
                return Math.round(((float) getWidth()) / FRICTION);
            default:
                return Math.round(((float) getHeight()) / FRICTION);
        }
    }

    private final void smoothScrollTo(int scrollValue, long duration) {
        smoothScrollTo(scrollValue, duration, 0, null);
    }

    private final void smoothScrollTo(int newScrollValue, long duration, long delayMillis, OnSmoothScrollFinishedListener listener) {
        int oldScrollValue;
        if (this.mCurrentSmoothScrollRunnable != null) {
            this.mCurrentSmoothScrollRunnable.stop();
        }
        switch (4.$SwitchMap$com$handmark$pulltorefresh$library$PullToRefreshBase$Orientation[getPullToRefreshScrollDirection().ordinal()]) {
            case 1:
                oldScrollValue = getScrollX();
                break;
            default:
                oldScrollValue = getScrollY();
                break;
        }
        if (oldScrollValue != newScrollValue) {
            if (this.mScrollAnimationInterpolator == null) {
                this.mScrollAnimationInterpolator = new DecelerateInterpolator();
            }
            this.mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(this, oldScrollValue, newScrollValue, duration, listener);
            if (delayMillis > 0) {
                postDelayed(this.mCurrentSmoothScrollRunnable, delayMillis);
            } else {
                post(this.mCurrentSmoothScrollRunnable);
            }
        }
    }

    private final void smoothScrollToAndBack(int y) {
        smoothScrollTo(y, 200, 0, new 3(this));
    }
}
