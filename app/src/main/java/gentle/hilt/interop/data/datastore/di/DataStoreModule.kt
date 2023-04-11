package gentle.hilt.interop.data.datastore.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gentle.hilt.interop.data.datastore.DataStoreManager
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataStoreModule {
    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext appContext: Context) = DataStoreManager(appContext)
}
