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
    abstract fun bindOneTimeAlarmRepeatStrategy(alarmRepeatStrategy: OneTimeAlarmRepeatStrategy): AlarmRepeatStrategy

    @IntoMap
    @IntKey(AlarmRepeat.MONDAY2FRIDAY)
    @Binds
    abstract fun bindMonday2FridayAlarmRepeatStrategy(alarmRepeatStrategy: Monday2FridayAlarmRepeatStrategy): AlarmRepeatStrategy

    @IntoMap
    @Binds
    @IntKey(AlarmRepeat.WORKING_DAY)
    abstract fun bindWorkingDayAlarmRepeatStrategy(alarmRepeatStrategy: WorkingDayAlarmRepeatStrategy): AlarmRepeatStrategy

    @IntoMap
    @Binds
    @IntKey(AlarmRepeat.EVERYDAY)
    abstract fun bindEverydayAlarmRepeatStrategy(alarmRepeatStrategy: EverydayAlarmRepeatStrategy): AlarmRepeatStrategy

    @IntoMap
    @Binds
    @IntKey(AlarmRepeat.STATUTORY_HOLIDAYS)
    abstract fun bindStatutoryHolidaysAlarmRepeatStrategy(alarmRepeatStrategy: StatutoryHolidaysAlarmRepeatStrategy): AlarmRepeatStrategy

    @IntoMap
    @Binds
    @IntKey(AlarmRepeat.REWARD)
    abstract fun bindRewardAlarmRepeatStrategy(alarmRepeatStrategy: RewardAlarmRepeatStrategy): AlarmRepeatStrategy


}