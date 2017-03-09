package p3ovingv2;

import java.time.Clock;
import java.util.LinkedList;

/**
 * This class implements functionality associated with
 * the CPU unit of the simulated system.
 */
public class Cpu {

	public LinkedList<Process> cpuQueue;
	public long maxCpuTime;
	public Statistics statistics;
	private Process activeProcess;

    /**
     * Creates a new CPU with the given parameters.
     * @param /cpuQueeue	The CPU queue to be used.
     * @param maxCpuTime	The Round Robin time quant to be used.
     * @param statistics	A reference to the statistics collector.
     */
    public Cpu(LinkedList<Process> cpuQueueToSet, long maxCpuTime, Statistics statistics) {
        this.cpuQueue = cpuQueueToSet;
		this.maxCpuTime = maxCpuTime;
		this.statistics = statistics;
    }

    /**
     * Adds a process to the CPU queue, and activates (switches in) the first process
     * in the CPU queue if the CPU is idle.
     * @param p		The process to be added to the CPU queue.
     * @param clock	The global time.
     * @return		The event causing the process that was activated to leave the CPU,
     *				or null	if no process was activated.
     */
    public Event insertProcess(Process p, long clock) {
		cpuQueue.add(p);
		p.addedToCpuQueue();
		if (cpuQueue.size() == 1 && activeProcess == null) {
			this.activeProcess = p;
    		statistics.nofProcessSwitches++;
			return switchProcess(clock);
		}
        return null;
    }

    /**
     * Activates (switches in) the first process in the CPU queue, if the queue is non-empty.
     * The process that was using the CPU, if any, is switched out and added to the back of
     * the CPU queue, in accordance with the Round Robin algorithm.
     * @param clock	The global time.
     * @return		The event causing the process that was activated to leave the CPU,
     *				or null	if no process was activated.
     */
    public Event switchProcess(long clock) {
		if (this.cpuQueue.size() > 0) {
			if (this.activeProcess != null) {
				Process sendToBack = getActiveProcess();
				this.cpuQueue.add(sendToBack);
			}
			Process toStart = this.cpuQueue.getFirst();
			this.activeProcess = toStart;
			return activeProcessLeft(clock);
		}
        return null;
    }

    /**
     * Called when the active process left the CPU (for example to perform I/O),
     * and a new process needs to be switched in.
     * @return	The event generated by the process switch, or null if no new
     *			process was switched in.
     */
    //Do we need this? (probs)
    public Event activeProcessLeft(long clock) {
		if (this.activeProcess == null) {
			return null;
		}
		Event switchEvent = new Event(Event.SWITCH_PROCESS, clock);
		return switchEvent;
    }

    /**
     * Returns the process currently using the CPU.
     * @return	The process currently using the CPU.
     */
    public Process getActiveProcess() {
		return activeProcess;
    }

    /**
     * This method is called when a discrete amount of time has passed.
     * @param timePassed	The amount of time that has passed since the last call to this method.
     */
    public void timePassed(long timePassed) {
		statistics.cpuQueueLengthTime += this.cpuQueue.size()*timePassed;
		if (this.cpuQueue.size() > statistics.cpuQueueLargestLength) {
			statistics.cpuQueueLargestLength = this.cpuQueue.size();
		}
    }
}
