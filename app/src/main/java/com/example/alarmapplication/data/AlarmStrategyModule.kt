package com.example.alarmapplication.data

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap

@Module
abstract class AlarmStrategyModule {
    @Binds
    abstract fun bindAlarmRepeatStrategyFactory(alarmRepeatStrategyFactory: AlarmRepeatStrategyFactoryImpl): AlarmRepeatStrategyFactory

    @IntoMap
    @IntKey(AlarmRepeat.ONE_TIME)
    @Binds
    abstract fun bindOneTimeAlarmRepeatStrategy(alarmRepeatStrategyFactory: OneTimeAlarmRepeatStrategy): AlarmRepeatStrategy

    @IntoMap
    @IntKey(AlarmRepeat.MONDAY2FRIDAY)
    @Binds
    abstract fun bindMonday2FridayAlarmRepeatStrategy(alarmRepeatStrategyFactory: Monday2FridayAlarmRepeatStrategy): AlarmRepeatStrategy

    companion object {
//        @IntoMap
//        @IntKey(AlarmRepeat.ONE_TIME)
//        @Provides
//        fun provideOneTimeAlarmRepeatStrategy(): AlarmRepeatStrategy {
//            return OneTimeAlarmRepeatStrategy()
//        }

//        @IntoMap
//        @IntKey(AlarmRepeat.MONDAY2FRIDAY)
//        @Provides
//        fun provideMonday2FridayAlarmRepeatStrategy(): AlarmRepeatStrategy {
//            return Monday2FridayAlarmRepeatStrategy()
//        }
    }


}