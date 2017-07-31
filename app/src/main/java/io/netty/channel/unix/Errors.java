package io.netty.channel.unix;

import io.netty.util.internal.EmptyArrays;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;

public final class Errors {
    public static final int ERRNO_EAGAIN_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errnoEAGAIN());
    public static final int ERRNO_EBADF_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errnoEBADF());
    public static final int ERRNO_ECONNRESET_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errnoECONNRESET());
    public static final int ERRNO_EINPROGRESS_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errnoEINPROGRESS());
    public static final int ERRNO_ENOTCONN_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errnoENOTCONN());
    public static final int ERRNO_EPIPE_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errnoEPIPE());
    public static final int ERRNO_EWOULDBLOCK_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errnoEWOULDBLOCK());
    private static final String[] ERRORS = new String[512];
    public static final int ERROR_EALREADY_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errorEALREADY());
    public static final int ERROR_ECONNREFUSED_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errorECONNREFUSED());
    public static final int ERROR_EISCONN_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errorEISCONN());
    public static final int ERROR_ENETUNREACH_NEGATIVE = (-ErrorsStaticallyReferencedJniMethods.errorENETUNREACH());

    static final class NativeConnectException extends ConnectException {
        private static final long serialVersionUID = -5532328671712318161L;
        private final int expectedErr;

        NativeConnectException(String method, int expectedErr) {
            super(method);
            this.expectedErr = expectedErr;
        }

        int expectedErr() {
            return this.expectedErr;
        }
    }

    public static final class NativeIoException extends IOException {
        private static final long serialVersionUID = 8222160204268655526L;
        private final int expectedErr;

        public NativeIoException(String method, int expectedErr) {
            super(method);
            this.expectedErr = expectedErr;
        }

        public int expectedErr() {
            return this.expectedErr;
        }
    }

    static {
        for (int i = 0; i < ERRORS.length; i++) {
            ERRORS[i] = ErrorsStaticallyReferencedJniMethods.strError(i);
        }
    }

    static void throwConnectException(String method, NativeConnectException refusedCause, int err) throws IOException {
        if (err == refusedCause.expectedErr()) {
            throw refusedCause;
        } else if (err == ERROR_EALREADY_NEGATIVE) {
            throw new ConnectionPendingException();
        } else if (err == ERROR_ENETUNREACH_NEGATIVE) {
            throw new NoRouteToHostException();
        } else if (err == ERROR_EISCONN_NEGATIVE) {
            throw new AlreadyConnectedException();
        } else {
            throw new ConnectException(method + "() failed: " + ERRORS[-err]);
        }
    }

    public static NativeIoException newConnectionResetException(String method, int errnoNegative) {
        NativeIoException exception = newIOException(method, errnoNegative);
        exception.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
        return exception;
    }

    public static NativeIoException newIOException(String method, int err) {
        return new NativeIoException(method + "() failed: " + ERRORS[-err], err);
    }

    public static int ioResult(String method, int err, NativeIoException resetCause, ClosedChannelException closedCause) throws IOException {
        if (err == ERRNO_EAGAIN_NEGATIVE || err == ERRNO_EWOULDBLOCK_NEGATIVE) {
            return 0;
        }
        if (err == resetCause.expectedErr()) {
            throw resetCause;
        } else if (err == ERRNO_EBADF_NEGATIVE) {
            throw closedCause;
        } else if (err == ERRNO_ENOTCONN_NEGATIVE) {
            throw new NotYetConnectedException();
        } else {
            throw newIOException(method, err);
        }
    }

    private Errors() {
    }
}
