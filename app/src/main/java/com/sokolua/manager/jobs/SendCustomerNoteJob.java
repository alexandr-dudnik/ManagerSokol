package com.sokolua.manager.jobs;

import com.sokolua.manager.data.managers.DataManager;
import com.sokolua.manager.data.storage.realm.NoteRealm;

public class SendCustomerNoteJob extends AbstractJob<NoteRealm> {

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
