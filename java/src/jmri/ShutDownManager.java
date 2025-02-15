package jmri;

import java.util.List;

import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.CheckForNull;
import jmri.beans.PropertyChangeProvider;

/**
 * Manage tasks to be completed when the program shuts down normally.
 * <p>
 * Implementations of this interface allow other objects to register and
 * deregister {@link ShutDownTask} objects, which are invoked in an orderly way
 * when the program is is commanded to terminate. There is no requirement a
 * ShutDownTask not interact with the user interface, and an assumption that,
 * barring a headless application, that ShutDownTasks may interact with the user
 * interface.
 * <p>
 * There can only be one instance of this operating, and it is generally
 * obtained via the instance manager.
 * <p>
 * ShutDownTasks should leave the system in a state that can continue, in case a
 * later task aborts the shutdown.
 * <p>
 * An instance of this is normally obtained from the instance manager, using may
 * assume that one is always present.
 *
 * @author Bob Jacobsen Copyright (C) 2008
 */
public interface ShutDownManager extends PropertyChangeProvider {

    /**
     * Register a task object for later execution. An attempt to register an
     * already registered task will be silently ignored.
     *
     * @param task the task to execute
     * @throws NullPointerException if the task is null
     */
    public void register(@Nonnull ShutDownTask task);

    /**
     * Register a task for verification that JMRI should stop. An attempt to
     * register an already register task will be silently ignored.
     *
     * @param task the verification task
     * @throws NullPointerException if the task is null
     */
    public void register(@Nonnull Callable<Boolean> task);

    /**
     * Register a task that runs when JMRI is stopping. An attempt to
     * register an already register task will be silently ignored.
     *
     * @param task the execution task
     * @throws NullPointerException if the task is null
     */
    public void register(@Nonnull Runnable task);

    /**
     * Deregister a task. Attempts to deregister a task that is not
     * registered are silently ignored.
     *
     * @param task the task not to execute
     */
    public void deregister(@CheckForNull ShutDownTask task);

    /**
     * Deregister a task. Attempts to deregister a task that is not
     * registered are silently ignored.
     *
     * @param task the task not to call
     */
    public void deregister(@CheckForNull Callable<Boolean> task);

    /**
     * Deregister a task. Attempts to deregister a task that is not
     * registered are silently ignored.
     *
     * @param task the task not to run
     */
    public void deregister(@CheckForNull Runnable task);

    @Nonnull
    public List<Callable<Boolean>> getCallables();

    @Nonnull
    public List<Runnable> getRunnables();

    /**
     * Run the shutdown tasks, and then terminate the program with status 210 if
     * not aborted. Does not return under normal circumstances. Returns false if
     * the shutdown was aborted by the user, in which case the program should
     * continue to operate.
     * <p>
     * By exiting the program with status 210, the batch file (MS Windows) or
     * shell script (Linux/macOS/UNIX) can catch the exit status and tell the
     * operating system to restart.
     */
    public void restartOS();

    /**
     * Run the shutdown tasks, and then terminate the program with status 100 if
     * not aborted. Does not return under normal circumstances. Returns false if
     * the shutdown was aborted by the user, in which case the program should
     * continue to operate.
     * <p>
     * By exiting the program with status 100, the batch file (MS Windows) or
     * shell script (Linux/macOS/UNIX) can catch the exit status and restart the
     * JMRI java program.
     */
    public void restart();

    /**
     * Run the shutdown tasks, and then terminate the program with status 200 if
     * not aborted. Does not return under normal circumstances. Returns false if
     * the shutdown was aborted by the user, in which case the program should
     * continue to operate.
     * <p>
     * By exiting the program with status 200, the batch file (MS Windows) or
     * shell script (Linux/macOS/UNIX) can catch the exit status and shutdown the OS
     */
    public void shutdownOS();

    /**
     * Run the shutdown tasks, and then terminate the program with status 0 if
     * not aborted. Does not return under normal circumstances. Returns false if
     * the shutdown was aborted by the user, in which case the program should
     * continue to operate.
     */
    public void shutdown();

    /**
     * Allow components that normally request confirmation to shutdown to
     * determine if the shutdown is already underway so as not to request
     * confirmation.
     *
     * @return true if shutting down or restarting
     */
    public boolean isShuttingDown();
}
