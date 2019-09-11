package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;

public class SendCustomerNoteJob extends AbstractJob {

    public SendCustomerNoteJob(String noteId) {
        super(noteId
                ,"CustomersData"
                ,JobPriority.HIGH);

    }

    @Override
    public void onRun() throws Throwable {
        runJob(DataManager.getInstance().sendSingleNote(jobId));
    }

}
