package hlx.launch.game;

import android.os.Build.VERSION;
import com.huluxia.utils.UtilsFile;
import com.huluxia.utils.ah;
import com.huluxia.widget.Constants;
import com.huluxia.widget.Constants.ReStartSoftFlag;
import hlx.data.localstore.a;

/* compiled from: MCLaunchGame */
public class c {
    private static c bRa;
    private int bQU = ah.KZ().P(a.bKi, -1);
    private int bQV = ah.KZ().P(a.bKr, 1);
    private int bQW = ah.KZ().P(a.bKw, -1);
    private boolean bQX = ah.KZ().j(a.bKk, true);
    private boolean bQY = ah.KZ().j(a.bKm, false);
    private boolean bQZ = ah.KZ().j(a.bKo, true);

    public static synchronized c Sg() {
        c cVar;
        synchronized (c.class) {
            if (bRa == null) {
                bRa = new c();
            }
            cVar = bRa;
        }
        return cVar;
    }

    public int Sh() {
        return this.bQU;
    }

    public void mG(int inputLaunchGameIndex) {
        if (!(inputLaunchGameIndex == 0 || inputLaunchGameIndex == 1 || inputLaunchGameIndex == 2 || inputLaunchGameIndex == 3 || inputLaunchGameIndex == 4 || inputLaunchGameIndex == 5 || inputLaunchGameIndex == 7 || inputLaunchGameIndex == 8)) {
            inputLaunchGameIndex = -1;
        }
        this.bQU = inputLaunchGameIndex;
        ah.KZ().Q(a.bKi, inputLaunchGameIndex);
    }

    public String dx(boolean inputShortFlag) {
        switch (this.bQU) {
            case -1:
                return a.bJg;
            case 0:
                return inputShortFlag ? a.bJr : a.bJh;
            case 1:
                return inputShortFlag ? a.bJs : a.bJi;
            case 2:
                if (VERSION.SDK_INT < 11) {
                    return inputShortFlag ? a.bJt : a.bJj;
                }
                return inputShortFlag ? a.bJt : a.bJk;
            case 3:
                return inputShortFlag ? "0.13" : a.bJl;
            case 4:
                return inputShortFlag ? "0.13" : a.bJm;
            case 5:
                return inputShortFlag ? "0.14" : a.bJn;
            case 7:
                return inputShortFlag ? a.bJy : a.bJo;
            case 8:
                return inputShortFlag ? a.bJz : a.bJp;
            default:
                return null;
        }
    }

    public boolean Si() {
        return this.bQX;
    }

    public void dy(boolean mOpenFloatWindow) {
        this.bQX = mOpenFloatWindow;
        ah.KZ().k(a.bKk, mOpenFloatWindow);
    }

    public boolean Sj() {
        return this.bQY;
    }

    public void dz(boolean mRepairBlackScreen) {
        this.bQY = mRepairBlackScreen;
        ah.KZ().k(a.bKm, mRepairBlackScreen);
    }

    public boolean Sk() {
        return this.bQZ;
    }

    public void dA(boolean isClearMemory) {
        this.bQZ = isClearMemory;
        ah.KZ().k(a.bKo, isClearMemory);
    }

    public int Sl() {
        return this.bQV;
    }

    public void mH(int progress) {
        this.bQV = progress;
        ah.KZ().Q(a.bKr, progress);
    }

    public void mI(int lastVersionIndex) {
        if (!(lastVersionIndex == 0 || lastVersionIndex == 1 || lastVersionIndex == 2 || lastVersionIndex == 3 || lastVersionIndex == 4 || lastVersionIndex == 5 || lastVersionIndex == 7)) {
            lastVersionIndex = -1;
        }
        this.bQW = lastVersionIndex;
        ah.KZ().Q(a.bKw, lastVersionIndex);
    }

    public int Sm() {
        return this.bQW;
    }

    public static boolean d(String ver, int intVer, boolean isJs) {
        boolean isMatch = false;
        switch (intVer) {
            case 0:
                isMatch = ver.contains(String.valueOf(ReStartSoftFlag.MC_RESTART_V0105.Value()));
                break;
            case 1:
                isMatch = ver.contains(String.valueOf(ReStartSoftFlag.MC_RESTART_V0111.Value()));
                break;
            case 2:
                isMatch = ver.contains(String.valueOf(ReStartSoftFlag.MC_RESTART_V0121.Value()));
                break;
            case 3:
                isMatch = ver.contains(String.valueOf(ReStartSoftFlag.MC_RESTART_V0130.Value()));
                break;
            case 4:
                if (!isJs) {
                    isMatch = ver.contains(String.valueOf(ReStartSoftFlag.MC_RESTART_V0130.Value()));
                    break;
                }
                isMatch = ver.contains(String.valueOf(ReStartSoftFlag.MC_RESTART_V0131.Value()));
                break;
            case 5:
                isMatch = ver.contains(String.valueOf(ReStartSoftFlag.MC_RESTART_V0140.Value()));
                break;
            case 7:
                isMatch = ver.contains(String.valueOf(ReStartSoftFlag.MC_RESTART_V0150.Value()));
                break;
        }
        if (ver == null || ver.length() == 0 || isMatch) {
            return true;
        }
        return false;
    }

    public static boolean aD(String allVer, String subVer) {
        return allVer == null || allVer.length() == 0 || allVer.contains(subVer);
    }

    public String Sn() {
        switch (this.bQU) {
            case 0:
                return UtilsFile.getRootPath() + Constants.bsu + ".zip";
            case 1:
                return UtilsFile.getRootPath() + Constants.bsv + ".zip";
            case 2:
                return UtilsFile.getRootPath() + Constants.bsw + ".zip";
            case 3:
                return UtilsFile.getRootPath() + Constants.bsx + ".zip";
            case 4:
                return UtilsFile.getRootPath() + Constants.bsy + ".zip";
            default:
                return "";
        }
    }
}
