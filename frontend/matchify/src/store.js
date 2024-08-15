import { configureStore } from '@reduxjs/toolkit';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/lib/storage'; // defaults to localStorage for web
import createSagaMiddleware from 'redux-saga';
import rootSaga from './Sagas';
import rootReducer from './Reducer';

const sagaMiddleware = createSagaMiddleware();

// Redux Persist configuration
const persistConfig = {
  key: 'root',
  storage,
};

// Create persisted reducer
const persistedReducer = persistReducer(persistConfig, rootReducer);

// Create Redux store
const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) => getDefaultMiddleware({serializableCheck: false}).concat(sagaMiddleware),
});

// Run sagas
sagaMiddleware.run(rootSaga);

// Create persistor
const persistor = persistStore(store);

export { store, persistor };