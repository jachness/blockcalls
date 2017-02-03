package com.jachness.blockcalls;

import com.jachness.blockcalls.modules.AppModule;
import com.jachness.blockcalls.modules.BlockModule;
import com.jachness.blockcalls.modules.DAOModule;
import com.jachness.blockcalls.services.BlackListCheckerTest;
import com.jachness.blockcalls.services.ContactCheckerTest;
import com.jachness.blockcalls.services.EndCallServiceTest;
import com.jachness.blockcalls.services.MasterCheckerTest;
import com.jachness.blockcalls.services.MatcherServiceTest;
import com.jachness.blockcalls.services.PrivateNumberCheckerTest;
import com.jachness.blockcalls.services.QuickBlackListCheckerTest;
import com.jachness.blockcalls.services.ValidatorServiceTest;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by jachness on 11/11/2016.
 */
@Singleton
@Component(modules = {BlockModule.class, AppModule.class, DAOModule.class})
public interface AllComponentTest {
    void inject(PrivateNumberCheckerTest privateNumberCheckerTest);

    void inject(BlackListCheckerTest blackListCheckerTest);

    void inject(ContactCheckerTest contactCheckerTest);

    void inject(MasterCheckerTest masterCheckerTest);

    void inject(ValidatorServiceTest validatorServiceTest);

    void inject(MatcherServiceTest matcherServiceTest);

    void inject(EndCallServiceTest endCallServiceTest);

    void inject(QuickBlackListCheckerTest quickBlackListCheckerTest);
}
